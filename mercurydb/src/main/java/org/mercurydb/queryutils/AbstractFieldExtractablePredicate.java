package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T> // TODO seriously this is confusing. what is T?
 */
public abstract class AbstractFieldExtractablePredicate<T> implements FieldExtractableSeed<T>, HgPredicate<T>  {
    protected final FieldExtractableSeed<T> _fwdFE;

    public AbstractFieldExtractablePredicate(FieldExtractableSeed<T> fe) {
        this._fwdFE = fe;
    }

    @Override
    public Class<?> getContainerClass() {
        return _fwdFE.getContainerClass();
    }

    @Override
    public Object extractField(Object o) {
        return _fwdFE.extractField(o);
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
    // TODO should this be TableID<?> or something else?
    public TableID<?> getContainerId() {
        return _fwdFE.getContainerId();
    }

    public HgStream<T> getDefaultStream() {
        return _fwdFE.getDefaultStream();
    }
}
