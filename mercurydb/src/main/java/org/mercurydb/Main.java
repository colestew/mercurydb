package org.mercurydb;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.mercurydb.MercuryBootstrap;

public class Main {
	
	public static void main(String[] args) 
			throws NotFoundException, CannotCompileException, MalformedURLException {
		
		if (args.length < 3 || args.length > 4) {
			printHelp();
		}

		String srcPackage = args[0];
		String dbPackage = args[1];
		String srcDir = args[2];
		boolean insertHooks = false;

		if (args.length == 4) {
			if (args[3].equals("--insert-hooks")) {
				insertHooks = true;
			} else {
				printHelp();
			}
		}
		
		MercuryBootstrap bs = new MercuryBootstrap(srcPackage, dbPackage, srcDir, insertHooks);
		bs.performBootstrap();
	}

	public static void printHelp() {
		System.out.println("usage javadb srcPackage dbPackage rootDir [--insert-hooks]");
		System.exit(1);
	}
}
