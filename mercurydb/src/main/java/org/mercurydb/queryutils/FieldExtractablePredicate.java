package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * FieldExtractablePredicate is used to specify user-defined predicates for queries.
 * You can instantiate one of these by using, e.g., CustomerTable.predicate.x(x -> x < 5)
 *
 */
public class FieldExtractablePredicate<T> extends AbstractFieldExtractablePredicate<T> {

    public final HgPredicate predicate;

    public FieldExtractablePredicate(FieldExtractableSeed fe, HgPredicate<Object> predicate) {
        super(fe);
        this.predicate = predicate;
    }

    public boolean test(Object value) {
        return predicate.test(value);
    }
}
