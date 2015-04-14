package org.mercurydb.queryutils;

/**
 * FieldExtractablePredicate is used to specify user-defined predicates for queries.
 * You can instantiate one of these by using, e.g., CustomerTable.predicate.x(x -> x < 5)
 */
public class FieldExtractablePredicate<T, F> extends AbstractFieldExtractablePredicate<T, F> {
    public final HgPredicate predicate;

    public FieldExtractablePredicate(FieldExtractableSeed fe, HgPredicate<F> predicate) {
        super(fe);
        this.predicate = predicate;
    }

    public boolean test(Object value) {
        return predicate.test(value);
    }
}
