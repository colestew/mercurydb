package org.mercurydb;

import javassist.*;
import org.mercurydb.annotations.AnnotationPair;
import org.mercurydb.annotations.HgUpdate;
import org.mercurydb.annotations.HgValue;

import java.io.IOException;
import java.util.Map;

public class BytecodeModifier {
    private CtClass _srcCtClass;
    private Class<?> _srcClass;
    private String _tableClass;
    private String _hooksBaseDir;

    public BytecodeModifier(CtClass srcCtClass, Class<?> srcClass, String tableClass, String hooksBaseDir) {
        _srcCtClass = srcCtClass;
        _srcClass = srcClass;
        _tableClass = tableClass;
        _hooksBaseDir = hooksBaseDir;
    }

    public void modify() throws CannotCompileException, IOException, NotFoundException {
        String constructorHook = _tableClass + ".insert(this);";
        for (CtConstructor con : _srcCtClass.getConstructors()) {
            con.insertAfter(constructorHook);
        }

        insertMethodHooks();
        _srcCtClass.writeFile(_hooksBaseDir);
    }

    private void insertMethodHooks() throws CannotCompileException {
        Map<String, AnnotationPair<HgValue>> valueMap = MercuryBootstrap.getHgValues(_srcClass);

        for (CtMethod m : _srcCtClass.getMethods()) {
            HgUpdate updateAnn = null;

            try {
                updateAnn = (HgUpdate) m.getAnnotation(HgUpdate.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (updateAnn == null) continue;

            for (String value : updateAnn.value()) {
                if (valueMap.containsKey(value)) {
                    AnnotationPair<HgValue> pair = valueMap.get(value);

                    String removeHook = String.format("%s.removeStaleValue%s(this);",
                            _tableClass,
                            Utils.upperFirst(value));
                    String updateHook = String.format("%s.updateNewValue%s(this);",
                            _tableClass,
                            Utils.upperFirst(value));

                    m.insertBefore(removeHook);
                    m.insertAfter(updateHook);

                } else {
                    throw new IllegalStateException(
                            String.format("Cannot apply @HgUpdate(\"%s\") when no @HgValue(\"%s\") exists!",
                                    value, value));
                }
            }
        }
    }
}
