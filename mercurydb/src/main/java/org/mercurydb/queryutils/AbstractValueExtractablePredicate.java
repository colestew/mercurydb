package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * // TODO class documentation please
 *
 * @param <T> // TODO seriously this is confusing. what is T?
 */
public abstract class AbstractValueExtractablePredicate<T, F>
        extends ValueExtractableSeed<T> implements HgPredicate<F> {
    protected final ValueExtractableSeed<T> _fwdFE;

    public AbstractValueExtractablePredicate(ValueExtractableSeed<T> fe) {
        super(fe.getTableId());
        this._fwdFE = fe;
    }

    @Override
    public Class<?> getContainerClass() {
        return _fwdFE.getContainerClass();
    }

    @Override
    public Object extractValue(Object o) {
        return _fwdFE.extractValue(o);
    }

    @Override
    public boolean isIndexed() {
        return _fwdFE.isIndexed();
    }

    @Override
    public Map<Object, Set<Object>> getIndex() {
        return _fwdFE.getIndex();
    }

    @Override
    public TableID<T> getTableId() {
        return _fwdFE.getTableId();
    }

    public HgStream<T> getDefaultStream() {
        return _fwdFE.getDefaultStream();
    }
}
