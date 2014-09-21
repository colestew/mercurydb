package org.mercurydb;

import java.io.File;

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
}
