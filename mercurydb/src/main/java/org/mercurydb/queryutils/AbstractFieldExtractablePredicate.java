package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T> // TODO seriously this is confusing. what is T?
 */
public abstract class AbstractFieldExtractablePredicate<T,F>
        extends FieldExtractableSeed<T> implements HgPredicate<F>  {
    protected final FieldExtractableSeed<T> _fwdFE;

    public AbstractFieldExtractablePredicate(FieldExtractableSeed<T> fe) {
        super(fe.getTableId());
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
    public TableID<T> getTableId() {
        return _fwdFE.getTableId();
    }

    public HgStream<T> getDefaultStream() {
        return _fwdFE.getDefaultStream();
    }
}
