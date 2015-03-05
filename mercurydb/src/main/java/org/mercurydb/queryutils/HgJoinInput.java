package org.mercurydb.queryutils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class HgJoinInput
        extends HgStream<HgTuple> implements FieldExtractable<Object, Object> {
    protected FieldExtractable<Object, Object> _fwdFE;
    protected Set<Class<?>> _containedTypes;

    public HgJoinInput(HgJoinInput a, HgJoinInput b) {
        super(0);
        a._containedTypes.addAll(b._containedTypes);
        this._containedTypes = a._containedTypes;
    }

    public HgJoinInput(FieldExtractable<Object, Object> fe) {
        super(0);
        setJoinKey(fe);
        this._containedTypes = new HashSet<Class<?>>();
        _containedTypes.add(fe.getContainerClass());
    }

    @SuppressWarnings("unchecked")
    public void setJoinKey(FieldExtractable<?, ?> fe) {
        this._fwdFE = (FieldExtractable<Object, Object>) fe;
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
        HgTuple jr = (HgTuple) instance;
        return _fwdFE.extractField(jr.get(_fwdFE.getContainerClass()));
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
                return HgJoinInput.this.hasNext();
            }

            @Override
            public Object next() {
                return HgJoinInput.this.next().get(_fwdFE.getContainerClass());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <F> HgJoinInput createJoinInput(
            FieldExtractable<?, F> fe,
            final HgStream<?> stream) {
        FieldExtractable<Object, Object> feo = (FieldExtractable<Object, Object>) fe;
        return new HgJoinInput(feo) {

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
