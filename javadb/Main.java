package javadb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

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

public class Main {

	@Index
	public int fooey;
	public static void main(String[] args) throws NotFoundException, CannotCompileException {

		if (args.length < 2 || args.length > 3) {
			printHelp();
		}

		File classDirectory = new File(args[0]);
		File outDirectory = new File(args[1]);

		boolean insertHooks = false;

		if (args.length == 3) {
			if (args[2].equals("--insert-hooks")) {
				insertHooks = true;
			} else {
				printHelp();
			}
		}

		if (!outDirectory.exists()) {
			outDirectory.mkdirs();
		}

		try {
			ClassPool cp = ClassPool.getDefault();

			// Convert File to a URL
			URL url = classDirectory.toURI().toURL();
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			ClassLoader cl = new URLClassLoader(urls);

			// Load in the class; MyClass.class should be located in
			// the directory file:/c:/myclasses/com/mycompany
			Collection<File> classFiles = FileUtils.listFiles(classDirectory,
					FileFilterUtils.suffixFileFilter(".class"),
					TrueFileFilter.INSTANCE);

			Collection<File> tableFiles = new ArrayList<File>();

			Collection<String> usedClassFiles = new ArrayList<String>();

			/*
			 *  Create Java tables (*Table.java files)
			 */
			for (File f : classFiles) {
				String outFileName = f.getPath().replace(".class", "Table.java");
				outFileName = outFileName.replace(classDirectory.getPath(),
						outDirectory.getName());
				File outFile = new File(outFileName);
				String packageName = toPackage(f.getPath());

				// Load class
				Class<? extends Object> cls = cl.loadClass(packageName);

				if (!cls.isMemberClass() && !cls.isLocalClass() && !cls.isAnonymousClass()) {
					tableFiles.add(outFile);
					usedClassFiles.add(packageName);
					System.out.println("Extracting " + packageName + " to " + outFile);

					// Write out Java file
					ClassExtractor extractor = new ClassExtractor(cls);
					extractor.extract(outFile);

				}
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
			if (insertHooks) {
				for (String packageName : usedClassFiles) {
					System.out.println("Adding insert hook to " + packageName);
					CtClass ctCls = cp.get(packageName);
					ClassModifier modifier = new ClassModifier(ctCls);
					modifier.modify(classDirectory, outDirectory);
				}
			}

			//System.out.println("size of class extractor table: " + ClassExtractorTable.table.size());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done.");
	}

	public static String toPackage(String path) {
		int lastDot = path.lastIndexOf('.');
		if (lastDot != -1) {
			path = path.substring(0, lastDot);
		}
		return path.replace(File.separatorChar, '.');
	}

	public static void printHelp() {
		System.out.println("usage javadb.Main class_directory output_directory [--insert-hooks]");
		System.exit(1);
	}
}
