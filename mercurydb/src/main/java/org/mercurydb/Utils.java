package org.mercurydb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class Utils {

	public static String upperFirst(String str) {
		char upperFirst = Character.toUpperCase(str.charAt(0));
		String ret = str.length() > 1
				? upperFirst + str.substring(1)
						: ""+upperFirst;
				return ret;
	}

	public static File toFile(Class<?> cls) {
		return new File(cls.getName().replace('.', '/') + ".class");
	}

	public static Class<?> toClass(ClassLoader cl, File classFile) throws ClassNotFoundException {
		String packageName = Utils.toPackage(classFile.getPath());
		Class<? extends Object> cls = cl.loadClass(packageName);
		return cls;
	}

	public static String toPackage(File f) {
		return toPackage(f.getPath());
	}

	public static Class<?> loadClass(ClassLoader cl, File f) {
		try {
			return cl.loadClass(Utils.toPackage(f.getPath()));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public static String toPackage(String path) {
		int lastDot = path.lastIndexOf('.');
		if (lastDot != -1) {
			path = path.substring(0, lastDot);
		}
		return path.replace(File.separatorChar, '.');
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
