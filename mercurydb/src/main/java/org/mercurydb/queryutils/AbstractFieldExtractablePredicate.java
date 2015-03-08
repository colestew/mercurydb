package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * Created by colestewart on 3/7/15.
 */
public abstract class AbstractFieldExtractablePredicate<T> implements FieldExtractableSeed<T>, HgPredicate<T>  {
    protected final FieldExtractableSeed<T> _fwdFE;

    public AbstractFieldExtractablePredicate(FieldExtractableSeed fe) {
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
    public int getContainerId() {
        return _fwdFE.getContainerId();
    }

    public HgStream<T> getDefaultStream() {
        return _fwdFE.getDefaultStream();
    }
}
