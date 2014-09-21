package org.mercurydb;

import java.io.File;
import java.net.MalformedURLException;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.mercurydb.MercuryBootstrap;

public class Main {
	
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
