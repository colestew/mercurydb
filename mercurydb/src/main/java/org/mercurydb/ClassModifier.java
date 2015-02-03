package org.mercurydb;

import java.io.File;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ClassModifier {
	public final CtClass c;

	public ClassModifier(CtClass c) {
		this.c = c;
	}

	public void modify(File inPackage, File outDirectory) 
			throws CannotCompileException, IOException, NotFoundException {
		
		String newPackage = Utils.toPackage(outDirectory.getPath());
		String oldPackage = Utils.toPackage(inPackage.getPath());
		String hook = c.getName().replace(oldPackage, newPackage) + "Table";
		
		for (CtConstructor con : c.getConstructors()) {
			con.insertAfter(hook + ".insert(this);");
		}
		
		for (CtField cf : c.getFields()) {
			if (!cf.hasAnnotation(org.mercurydb.Index.class)) continue;
			
			String methodName = "set" + Utils.upperFirst(cf.getName());
			try {
				// If method does not exist. This statement will throw an exception
				CtMethod cm = c.getDeclaredMethod(methodName);
				// If we get this far, add the hook
				cm.insertAfter(hook + "." + methodName + "(this, " + cf.getName() + ");");
			} catch (NotFoundException e) {
				System.err.println("Warning: No set method found for indexed field " + c.getName() + "." + cf.getName());
			} catch (SecurityException e) {
				System.err.println("Warning: " + e.getMessage());
			}
		}

		c.writeFile();
	}
}
