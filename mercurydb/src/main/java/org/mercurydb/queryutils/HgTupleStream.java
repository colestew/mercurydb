package org.mercurydb.queryutils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class HgTupleStream
        extends HgStream<HgTuple> implements FieldExtractable {
    protected FieldExtractable _fwdFE;
    protected final Set<Class<?>> _containedTypes;

    public HgTupleStream(HgTupleStream o) {
        this(o._fwdFE);

    }

    public HgTupleStream() {
        super(0);
        this._containedTypes = new HashSet<>();
    }

    public HgTupleStream(HgTupleStream a, HgTupleStream b) {
        this();
        _containedTypes.addAll(a._containedTypes);
        _containedTypes.addAll(b._containedTypes);
        a._containedTypes.addAll(b._containedTypes);
    }

    public HgStream<HgTuple> getDefaultStream() {
        return this;
    }

    public HgTupleStream(FieldExtractable fe, Set<Class<?>> containedTypes) {
        super(0);
        this._fwdFE = fe;
        this._containedTypes = containedTypes;
    }

    public HgTupleStream(FieldExtractable fe) {
        super(0);
        this._fwdFE = fe;
        this._containedTypes = new HashSet<Class<?>>();
        _containedTypes.add(fe.getContainerClass());
    }

    public FieldExtractable getFieldExtractor() {
        return _fwdFE;
    }

    @SuppressWarnings("unchecked")
    public void setJoinKey(FieldExtractable fe) {
        this._fwdFE = fe;
    }

    public Set<? extends Class<?>> getContainedTypes() {
        return _containedTypes;
    }

    @Override
    public Class<?> getContainerClass() {
        return _fwdFE.getContainerClass();
    }

    @Override
    public Object extractField(Object instance) {
        if (instance instanceof HgTuple) {
            HgTuple t = (HgTuple) instance;
            return _fwdFE.extractField(t.get(_fwdFE.getContainerClass()));
        }
        throw new IllegalArgumentException("Argument to HgTupleStream.extractField() should be an HgTuple!");
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

    public Iterator<Object> getObjectIterator() {
        return new Iterator<Object>() {

            @Override
            public boolean hasNext() {
                return HgTupleStream.this.hasNext();
            }

            @Override
            public Object next() {
                return HgTupleStream.this.next().get(_fwdFE.getContainerClass());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <F> HgTupleStream createJoinInput(
            FieldExtractable fe,
            final HgStream<?> stream) {
        FieldExtractable feo = fe;
        return new HgTupleStream(feo) {

            @Override
            public boolean hasNext() {
                return stream.hasNext();
            }

            @Override
            public HgTuple next() {
                return HgTuple.singleton(this, stream.next());
            }

            @Override
            public void reset() {
                // TODO Auto-generated method stub
                stream.reset();
            }

        };
    }
}
