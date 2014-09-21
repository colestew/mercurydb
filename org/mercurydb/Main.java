package org.mercurydb;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

	private static Predicate<Class<?>> supportedClassCheck = cls -> 
	!cls.isMemberClass() && !cls.isLocalClass() && !cls.isAnonymousClass();


	public static void main(String[] args) throws NotFoundException, CannotCompileException, MalformedURLException {

		if (args.length < 2 || args.length > 3) {
			printHelp();
		}

		File srcDir = new File(args[0]);
		File outDir = new File(args[1]);

		boolean insertHooks = false;

		if (args.length == 3) {
			if (args[2].equals("--insert-hooks")) {
				insertHooks = true;
			} else {
				printHelp();
			}
		}

		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		
		MercuryBootstrap bs = new MercuryBootstrap(srcDir, outDir, insertHooks) ;
		bs.performBootstrap();
	}

	public static void printHelp() {
		System.out.println("usage javadb.Main class_directory output_directory [--insert-hooks]");
		System.exit(1);
	}
}
