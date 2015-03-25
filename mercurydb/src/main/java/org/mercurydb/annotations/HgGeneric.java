package org.mercurydb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/**
 * Mark a class as generic by recording the type of the generic parameter
 * to be recalled at runtime.
 *
 * TODO This is not sufficient to capture generics with multiple arguments or nested generic types.
 */
public @interface HgGeneric {
    Class<?> value();
}
