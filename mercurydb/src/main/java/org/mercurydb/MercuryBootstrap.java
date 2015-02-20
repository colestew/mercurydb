package org.mercurydb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.google.common.base.Preconditions;

public class MercuryBootstrap {

	/**
	 * Predicate for classes which can be properly
	 * mapped to output tables.
	 */
	private static Predicate<Class<?>> supportedClassCheck = cls -> !cls.isMemberClass() 
			&& !cls.isLocalClass() 
			&& !cls.isAnonymousClass();

	/**
	 * source directory for classes
	 */
	private final String _srcPackage;

	/**
	 * Output package for generated code
	 */
	private final String _outPackage;

	/**
	 * output directory for tables
	 */
	private final String _srcJavaDir;

	/**
	 * Primary constructor for MercuryBootstrap.
	 * 
	 * @param srcPackage    the input package for client code
	 * @param outPackage    the output root package for generated code
	 * @param rootDir       the root directory for java files
	 * @param insertHooks   if true, inserts db ops into source bytecode
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public MercuryBootstrap(String srcPackage, String outPackage, String rootDir) 
			throws NotFoundException, CannotCompileException {
		this._srcPackage = srcPackage;
		this._outPackage = outPackage;
		this._srcJavaDir = rootDir;
	}

	/**
	 * Retrieves all classes that can be converted into table classes
	 * by this tool.
	 * 
	 * @return supported classes
	 */
	public Collection<Class<?>> getSupportedClasses() {
		// Fetch appropriate class files
		Collection<Class<?>> classes = Collections.emptyList();
		try {
			classes = Arrays.asList(Utils.getClasses(_srcPackage));
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		// filter classes so we only have supported class files
		classes = classes.stream()
				.filter(c -> c != null && supportedClassCheck.test(c))
				.collect(Collectors.toList());

		return classes;
	}

	/**
	 * Performs the bootstrap operation. This is everything. The
	 * cat's meow. Namely, it fetches all class files in the
	 * source directory and converts them into class objects. Then
	 */
	public void generateTables() {

		// Fetch appropriate class files
		Collection<Class<?>> classes = getSupportedClasses();

		// if no classes are supported exit
		if (classes.isEmpty()) {
			System.out.println("No supported .class files found in " + _srcPackage);
			System.exit(1);
		}

		// startup a collection of table files we generate
		Collection<String> tableFiles = new ArrayList<>();
		// and create a map of input package classes to their subclasses
		Map<Class<?>, List<Class<?>>> subClassMap = getSubclasses(classes);

		// now iterate over each class and generate the tables
		for (Class<?> cls : classes) {

			// fetch the subclass table names
			Collection<String> subTables = Collections.emptyList();
			if (subClassMap.containsKey(cls)) {
				subTables = subClassMap.get(cls).stream()
						.map(c -> toTableName(c))
						.collect(Collectors.<String>toList());
			}

			// calculate required paths and packages for the new table
			String genTablePrefix = _srcJavaDir + '/' + _outPackage.replace('.', '/') +
					cls.getName().replace(_srcPackage, "").replace('.', '/');
			String tablePath = genTablePrefix + "Table.java";
			String tablePackage = _outPackage + cls.getPackage().getName().replace(_srcPackage, "");
			tableFiles.add(genTablePrefix + "Table.java");

			System.out.println("Extracting " + cls + " to " + tablePath + " in " + tablePackage);

			String superTable = subClassMap.containsKey(cls.getSuperclass()) ? toTableName(cls.getSuperclass()) : null;

			ClassToTableExtractor extractor;
			try {
				extractor = new ClassToTableExtractor(cls, superTable, subTables);
				extractor.extract(tablePath, tablePackage);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Inserts bytecode hooks in the classes found in the input package
	 * 
	 * @throws NotFoundException 
	 * @throws IOException 
	 * @throws CannotCompileException 
	 */
	public void insertBytecodeHooks() {
		Collection<Class<?>> classes = getSupportedClasses();
		ClassPool cp = ClassPool.getDefault();
		/*
		 *  Modify original bytecode with the insert hooks
		 */
		for (Class<?> cls : classes) {
			System.out.println("Adding insert hook to " + cls);
			try {
				CtClass ctCls = cp.get(cls.getName());
				ClassModifier modifier = new ClassModifier(ctCls, toTableName(cls)+"Table");
				modifier.modify();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (CannotCompileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Converts a class to an output package name. To be more specific,
	 * this method converts the class to a filename, replaces the source
	 * directory with the out directory, and converts that path to a package
	 * name. It is up to the template engine to append "Table" or whatever
	 * it wants to use for the table class names.
	 * 
	 * @param c  class to convert
	 * @return output package name
	 */
	private String toTableName(Class<?> c) {
		String filePrefix = _outPackage + c.getName().replace(_srcPackage, "");
		return filePrefix;
	}

	/**
	 * Method which returns a map of each class in the given collection
	 * to immediate subclasses of that classes that are also in the given collection.
	 * 
	 * @param classes  the collection of classes as restriction of i/o
	 * @return map of each class to its immediate subclasses
	 */
	private Map<Class<?>, List<Class<?>>> getSubclasses(Collection<Class<?>> classes) {

		Map<Class<?>, List<Class<?>>> subclassMap = new HashMap<>();
		for (Class<?> c : classes) {

			// Determine if superclass has a mapped table class
			Class<?> currC = c;
			if (classes.contains(currC.getSuperclass())) {

				// Now if this subclass is supported put it in the map
				List<Class<?>> subClasses = subclassMap.get(currC.getSuperclass());
				if (subClasses == null) {
					subClasses = new ArrayList<>();
					subclassMap.put(currC.getSuperclass(), subClasses);
				}
				subClasses.add(c);

				currC = c.getSuperclass();
			}
		}

		return subclassMap;
	}
}

