package org.mercurydb;

import javassist.*;
import org.mercurydb.annotations.HgIndex;

import java.io.IOException;

public class BytecodeModifier {
    private CtClass _srcClass;
    private String _tableClass;

    public BytecodeModifier(CtClass srcClass, String tableClass) {
        _srcClass = srcClass;
        _tableClass = tableClass;
    }

    public void modify() throws CannotCompileException, IOException, NotFoundException {
        String constructorHook = _tableClass + ".insert(this);";
        for (CtConstructor con : _srcClass.getConstructors()) {
            con.insertAfter(constructorHook);
        }

        for (CtField cf : _srcClass.getFields()) {
            if (!cf.hasAnnotation(HgIndex.class)) continue;

            String methodName = "update" + Utils.upperFirst(cf.getName());
            try {
                // If method does not exist. This statement will throw an exception
                CtMethod cm = _srcClass.getDeclaredMethod(methodName);
                // If we get this far, add the hook
                cm.insertAfter(_tableClass + "." + methodName + "(this, " + cf.getName() + ");");
            } catch (NotFoundException e) {
                System.err.println("Warning: No set method found for indexed field " + _srcClass.getName() + "." + cf.getName());
            } catch (SecurityException e) {
                System.err.println("Warning: " + e.getMessage());
            }
        }
        CtMethod method = CtNewMethod.make("public void fooey() {}", _srcClass);
        _srcClass.addMethod(method);
        _srcClass.writeFile("build/classes/main");
    }
}
