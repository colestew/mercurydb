package org.mercurydb;

import javassist.*;
import org.mercurydb.annotations.HgUpdate;

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

        for (CtMethod cm : _srcClass.getMethods()) {
            try {
                // TODO this method needs access to the HgValue fields
                // it either needs to be built here or needs to be precomputed
                // and passed in here. My vote goes to precomputed because
                // it has to be computed anyways in the classtotable process

                HgUpdate update = (HgUpdate)cm.getAnnotation(HgUpdate.class);

            } catch (ClassNotFoundException e) {

                // Why would this ever be thrown??
                e.printStackTrace();
            }

            // TODO remove old code below
//            if (!cm.hasAnnotation(HgUpdate.class)) continue;
//
//            String methodName = "update" + Utils.upperFirst(cf.getName());
//            try {
//                // If method does not exist. This statement will throw an exception
//                CtMethod cm = _srcClass.getDeclaredMethod(methodName);
//                // If we get this far, add the hook
//                cm.insertAfter(_tableClass + "." + methodName + "(this, " + cf.getName() + ");");
//            } catch (NotFoundException e) {
//                System.err.println("Warning: No set method found for indexed field " + _srcClass.getName() + "." + cf.getName());
//            } catch (SecurityException e) {
//                System.err.println("Warning: " + e.getMessage());
//            }
        }
        _srcClass.writeFile("build/classes/main");
    }
}
