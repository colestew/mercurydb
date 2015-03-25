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
 * If this Annotation is used on a field where it does not apply (not a generic type),
 * the generated code will fail to compile because a generic type parameter will be
 * added to a non-generic type.
 *
 * If this Annotation is omitted where it would be useful, the code will compile but
 * there will be an unchecked cast in the set[FieldName] method in the corresponding
 * Table* class, and you will lose the benefit of the IDE helping you with the
 * specific types involved.
 *
 * TODO This is not sufficient to capture generics with multiple arguments or nested generic types.
 */
public @interface HgGeneric {
    Class<?> value();
}
