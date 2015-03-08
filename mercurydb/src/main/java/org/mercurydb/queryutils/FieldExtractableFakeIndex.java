package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * Created by colestewart on 3/7/15.
 */
public class FieldExtractableFakeIndex implements FieldExtractable {
    private final FieldExtractable _fwdFE;
    private final Map<Object, Set<Object>> _index;

    public FieldExtractableFakeIndex(FieldExtractable srcFE, Map<Object, Set<Object>> index) {
        this._fwdFE = srcFE;
        this._index = index;
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
        return true;
    }

    @Override
    public Map<Object, Set<Object>> getIndex() {
        return _index;
    }

    @Override
    public int getContainerId() {
        return _fwdFE.getContainerId();
    }
}
