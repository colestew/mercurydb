package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * This represents a pairing of FieldExtractable with a value of the same
 * type as the field. It is intended to be used in HgDB.query and HgDB.filter
 * methods to specify which value you would like to match.
 * You can instantiate this class as, e.g., Customer.field.x(5);
 *
 * @param <T> Type that the table contains. (e.g. CustomerTable -> Customer)
 * @param <F> Field type.
 */
public class FieldExtractableValue<T, F> implements FieldExtractable<T, F> {

    private final FieldExtractable<T, F> _fwd;

    public final F value;

    public FieldExtractableValue(FieldExtractable<T, F> src, F value) {
        this._fwd = src;
        this.value = value;
    }

    @Override
    public Class<T> getContainerClass() {
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
}
