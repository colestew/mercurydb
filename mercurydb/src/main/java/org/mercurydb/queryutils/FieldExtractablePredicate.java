package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * FieldExtractablePredicate is used to specify user-defined predicates for queries.
 * You can instantiate one of these by using, e.g., CustomerTable.predicate.x(x -> x < 5)
 *
 * @param <T> Type that the table contains. (e.g. CustomerTable -> Customer)
 * @param <F> Field type.
 */
public class FieldExtractablePredicate<T, F> implements FieldExtractable<T, F> {

    private final FieldExtractable<T, F> _fwd;

    public final HgPredicate<F> predicate;

    public FieldExtractablePredicate(FieldExtractable<T, F> fe, HgPredicate<F> pred) {
        this._fwd = fe;
        this.predicate = pred;
    }

    @Override
    // TODO why Class<?>
    public Class<?> getContainerClass() {
        return _fwd.getContainerClass();
    }

    @Override
    public F extractField(T o) {
        return _fwd.extractField(o);
    }

    @Override
    public boolean isIndexed() {
        return _fwd.isIndexed();
    }

    @Override
    public Map<F, Set<T>> getIndex() {
        return _fwd.getIndex();
    }

    @Override
    public int getContainerId() {
        return _fwd.getContainerId();
    }

    public boolean test(F value) {
    	return predicate.test(value);
    }
}
