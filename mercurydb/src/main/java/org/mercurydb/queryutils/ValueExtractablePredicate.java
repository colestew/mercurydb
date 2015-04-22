package org.mercurydb.queryutils;

/**
 * FieldExtractablePredicate is used to specify user-defined predicates for queries.
 * You can instantiate one of these by using, e.g., CustomerTable.predicate.x(x -> x < 5)
 */
public class ValueExtractablePredicate<T, F> extends AbstractValueExtractablePredicate<T, F> {
    public final HgPredicate predicate;

    public ValueExtractablePredicate(ValueExtractableSeed fe, HgPredicate<F> predicate) {
        super(fe);
        this.predicate = predicate;
    }

    public boolean test(Object value) {
        return predicate.test(value);
    }
}
