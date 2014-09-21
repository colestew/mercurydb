package org.mercurydb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
	private final File _srcDir;

	/**
	 * output directory for tables
	 */
	private final File _outDir;

	/**
	 * Tells this class to insert hooks into the
	 * source bytecode
	 */
	private final boolean _insertHooks;

	/**
	 * Primary constructor for MercuryBootstrap.
	 * 
	 * @param srcClass  the input class directory for client code
	 * @param outDirectory    the output directory for the database code
	 * @param insertHooks     if true, inserts db ops into source bytecode
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public MercuryBootstrap(File srcClass, File outClass, boolean insertHooks) 
			throws NotFoundException, CannotCompileException {

		this._srcDir = srcClass;
		this._outDir = outClass;
		this._insertHooks = insertHooks;
	}

	/**
	 * Performs the bootstrap operation. This is everything. The
	 * cat's meow. Namely, it fetches all class files in the
	 * source directory and converts them into class objects. Then
	 */
	public void performBootstrap() {
		try {
			ClassPool cp = ClassPool.getDefault();

			URL url = _srcDir.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);

			// Fetch appropriate class files
			Collection<File> classFiles = FileUtils.listFiles(
					_srcDir,
					FileFilterUtils.suffixFileFilter(".class"),
					TrueFileFilter.INSTANCE);

			// Convert class files to class objects
			Collection<Class<?>> classes = classFiles.stream()
					.map(f -> Utils.loadClass(cl, f)) // could have null results
					.filter(c -> c != null && supportedClassCheck.test(c))
					.collect(Collectors.toList());


			Collection<File> tableFiles = new ArrayList<File>();

			Map<Class<?>, List<Class<?>>> subClassMap = getSubclasses(classes);

			/*
			 *  Create Java tables (*Table.java files)
			 */
			for (Class<?> cls : classes) {
				Collection<String> subTables = Collections.emptyList();

				if (subClassMap.containsKey(cls)) {
					subTables = subClassMap.get(cls).stream()
							.map(c -> toTablePrefix(c))
							.collect(Collectors.<String>toList());
				}

				File tableFile = new File(
						Utils.toFile(cls).getPath().replace(
								_srcDir.getPath(), 
								_outDir.getPath())
								.replace(".class", "Table.java"));
				tableFiles.add(tableFile);
				System.out.println("Extracting " + cls + " to " + tableFile);
				String superTable = subClassMap.containsKey(cls.getSuperclass()) ? 
						toTablePrefix(cls.getSuperclass()) : null;
						ClassExtractor extractor = new ClassExtractor(cls, superTable, subTables);
						extractor.extract(tableFile);
			}

			/*
			 *  Compile the Java tables (*Table.class files)
			 */
			System.out.println("Compiling tables...");
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(tableFiles);
			compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
			fileManager.close();
			System.out.println("Done compiling.");

			/*
			 *  Modify original bytecode with the insert hooks
			 */
			if (_insertHooks) {
				for (Class<?> cls : classes) {
					System.out.println("Adding insert hook to " + cls);
					CtClass ctCls = cp.get(cls.getName());
					ClassModifier modifier = new ClassModifier(ctCls);
					modifier.modify(_srcDir, _outDir);
				}
			}

			//System.out.println("size of class extractor table: " + ClassExtractorTable.table.size());
		} catch (IOException | NotFoundException | CannotCompileException e) {
			e.printStackTrace();
		} 

		System.out.println("Done.");
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
	private String toTablePrefix(Class<?> c) {
		String subClass = Utils.toFile(c).getPath();
		subClass = subClass.replace(_srcDir.getPath(), _outDir.getPath());
		return Utils.toPackage(subClass);
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

