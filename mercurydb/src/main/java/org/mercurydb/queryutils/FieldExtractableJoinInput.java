package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * This is the type of FieldExtractable intended to be used as inputs to the
 * HgDB.join method (you can access instances of this class as, e.g., CustomerTable.on.x).
 *
 * @param <T> Type that the table contains. (e.g. CustomerTable -> Customer)
 * @param <F> Field type.
 */
public class FieldExtractableJoinInput<T, F> implements FieldExtractable<T, F> {
    private FieldExtractable<T, F> _fwd;
    private HgStream<T> _stream;

    public FieldExtractableJoinInput(FieldExtractable<T, F> fe, HgStream<T> stream) {
        this._fwd = fe;
        this._stream = stream;
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

    // TODO merge this class with HgMonoStream
    public HgMonoStream<T> getMonoStream() {
        return new HgMonoStream<T>(_stream, this);
    }
}
