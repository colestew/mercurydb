package org.mercurydb.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Pairs a method with an annotation. Useful in MercuryBootstrap when feeding
 * the bytecode modifier and table extractor.
 */
public class AnnotationPair<T extends Annotation> {
    public final T annotation;
    public final Method method;

    public AnnotationPair(T annotation, Method method) {
        this.annotation = annotation;
        this.method = method;
    }
}
