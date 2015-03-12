package org.mercurydb.queryutils;

import java.util.*;

public abstract class HgTupleStream
        extends HgStream<HgTupleStream.HgTuple> implements FieldExtractable {

    // TODO document these fields
    protected FieldExtractable _fwdFE;
    protected final Map<TableID<?>, Integer> _containedTypes;
    private int tupleIndexCounter = 0;

//    public HgTupleStream as(TableID<?> id) {
//        if (1 == _containedTypes.size()) {
//            // complex surgery to replace the existing id
//            TableID<?> x = _containedTypes.keySet().iterator().next();
//            Integer i = _containedTypes.get(x);
//            _containedTypes.remove(x);
//            _containedTypes.put(id, i);
//            return this;
//        } else {
//            throw new IllegalStateException("Can't create an alias for an HgTupleStream of cardinality > 1.");
//        }
//    }

    public HgTupleStream(HgTupleStream o) {
        this(o._fwdFE);
    }

    public HgTupleStream() {
        super(0);
        this._containedTypes = new HashMap<>();
    }

    @Override
    public HgTupleStream joinOn(FieldExtractable fe) {
        this._fwdFE = fe;
        return this;
    }

    public HgTupleStream(Collection<TableID<?>> aids, Collection<TableID<?>> bids) {
        this();

        for (TableID<?> tid : aids) {
            addContainedType(tid);
        }

        for (TableID<?> tid : bids) {
            addContainedType(tid);
        }
    }

    public HgTupleStream(FieldExtractable fe, Set<TableID<?>> containedTypes) {
        super(0);
        this._fwdFE = fe;
        this._containedTypes = new HashMap<>();

        for (TableID<?> id: containedTypes) {
            addContainedType(id);
        }
    }

    public HgTupleStream(FieldExtractable fe) {
        super(0);
        this._fwdFE = fe;
        this._containedTypes = new HashMap<>();
        addContainedType(fe.getContainerId());
    }

    private void addContainedType(TableID<?> id) {
        if (!_containedTypes.containsKey(id)) {
            _containedTypes.put(id, tupleIndexCounter++);
        }
    }

    public FieldExtractable getFieldExtractor() {
        return _fwdFE;
    }

    @SuppressWarnings("unchecked")
    public void setJoinKey(FieldExtractable fe) {
        this._fwdFE = fe;
    }

    public Set<TableID<?>> getContainedIds() {
        return new HashSet<>(_containedTypes.keySet());
    }

    public boolean containsId(TableID<?> id) {
        return _containedTypes.containsKey(id);
    }

    @Override
    public Class<?> getContainerClass() {
        return _fwdFE.getContainerClass();
    }

    @Override
    public Object extractField(Object instance) {
        return _fwdFE.extractField(instance);
    }

    public Object extractFieldFromTuple(HgTuple t) {
        return _fwdFE.extractField(t.get(_fwdFE.getContainerId()));
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
    public TableID<?> getContainerId() {
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
                return HgTupleStream.this.next().get(_fwdFE.getContainerId());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static HgTupleStream createJoinInput(
            FieldExtractable fe,
            final HgStream<?> stream) {
        return new HgTupleStream(fe) {

            @Override
            public boolean hasNext() {
                return stream.hasNext();
            }

            @Override
            public HgTuple next() {
                return this.new HgTuple(stream.next());
            }

            @Override
            public void reset() {
                stream.reset();
            }
        };
    }


    public class HgTuple {
        private ArrayList<Object> _entries;

        public HgTuple() {
            _entries = new ArrayList<>();
        }

        public HgTuple(Object o) {
            this();
            _entries.add(o);
        }

        public HgTuple(TableID<?> s1, final Object o1, TableID<?> s2, final Object o2) {
            this();
            insertRecord(s1, o1);
            insertRecord(s2, o2);
        }

        private void insertRecord(TableID<?> s, Object o) {
            // fetch the tuple index from the contained types with the given id
            int tupleIndex = _containedTypes.get(s);

            // ensure we have the capacity
            _entries.ensureCapacity(tupleIndex+1);
            while (_entries.size() < tupleIndex+1) {
                _entries.add(null);
            }

            _entries.set(tupleIndex, o);
        }

        public<T> T get(TableID<T> id) {
            if (!_containedTypes.containsKey(id)) {
                throw new IllegalArgumentException("ID not present in HgTupleStream instance.");
            }
            return (T)_entries.get(_containedTypes.get(id));
        }

        public Object extractJoinedField() {
            return _fwdFE.extractField(this.get(_fwdFE.getContainerId()));
        }

        public Object extractJoinedEntry() {
            return get(_fwdFE.getContainerId());
        }
    }
}
