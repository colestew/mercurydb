package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

public class FieldExtractableDefinedPredicate<T, F>
        implements FieldExtractable<T, F> {
    private final FieldExtractable<T, F> _fwd;

    public FieldExtractableDefinedPredicate(FieldExtractable<T, F> fe) {
        this._fwd = fe;
    }

    @Override
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
}
