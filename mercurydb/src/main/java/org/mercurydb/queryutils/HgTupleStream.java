package org.mercurydb.queryutils;

import java.util.*;

@SuppressWarnings("unused")
public abstract class HgTupleStream
        extends HgStream<HgTupleStream.HgTuple> implements ValueExtractable {

    // TODO document these fields
    protected ValueExtractable _fwdFE;
    protected final Map<TableID<?>, Integer> _containedTypes;
    private int tupleIndexCounter = 0;

    public HgTupleStream(HgTupleStream o) {
        this(o._fwdFE);
    }

    public HgTupleStream() {
        this._containedTypes = new HashMap<>();
    }

    public HgTupleStream(Collection<TableID<?>> aids, Collection<TableID<?>> bids) {
        this();

        aids.forEach(this::addContainedType);
        bids.forEach(this::addContainedType);
    }

    public HgTupleStream(ValueExtractable fe, Set<TableID<?>> containedTypes) {
        this._fwdFE = fe;
        this._containedTypes = new HashMap<>();

        containedTypes.forEach(this::addContainedType);
    }

    @Override
    public HgTupleStream joinOn(ValueExtractable fe) {
        this._fwdFE = fe;
        return this;
    }

    public HgTupleStream(ValueExtractable fe) {
        this._fwdFE = fe;
        this._containedTypes = new HashMap<>();
        addContainedType(fe.getTableId());
    }

    private void addContainedType(TableID<?> id) {
        if (!_containedTypes.containsKey(id)) {
            _containedTypes.put(id, tupleIndexCounter++);
        }
    }

    public ValueExtractable getFieldExtractor() {
        return _fwdFE;
    }

    @SuppressWarnings("unchecked")
    public void setJoinKey(ValueExtractable fe) {
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
    public Object extractValue(Object instance) {
        return _fwdFE.extractValue(instance);
    }

    public Object extractFieldFromTuple(HgTuple t) {
        return _fwdFE.extractValue(t.get(_fwdFE.getTableId()));
    }

    @Override
    abstract public boolean isIndexed();

    @Override
    public Map<Object, Set<Object>> getIndex() {
        return _fwdFE.getIndex();
    }

    @Override
    public TableID<?> getTableId() {
        return _fwdFE.getTableId();
    }

    public Iterator<Object> getObjectIterator() {
        return new Iterator<Object>() {

            @Override
            public boolean hasNext() {
                return HgTupleStream.this.hasNext();
            }

            @Override
            public Object next() {
                return HgTupleStream.this.next().get(_fwdFE.getTableId());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static HgTupleStream createJoinInput(
            final ValueExtractable fe,
            final HgStream<?> stream,
            final boolean streamIsFiltered) {
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

            @Override
            public boolean isIndexed() {
                return !streamIsFiltered && fe.isIndexed();
            }
        };
    }

    public class HgTuple {
        private ArrayList<Object> _entries = new ArrayList<>();

        public HgTuple(Object o) {
            _entries.add(o);
        }

        public HgTuple(TableID<?> aid, Object a, TableID<?> bid, Object b) {
            insertRecord(aid, a);
            insertRecord(bid, b);
        }

        public HgTuple(TableID<?> aid, Object a, HgTuple b) {
            insertRecord(aid, a);
            insertRecords(b);
        }

        public HgTuple(HgTuple a, HgTuple b) {
            for (TableID<?> id : a.getStream()._containedTypes.keySet()) {
                insertRecord(id, a.get(id));
            }

            insertRecords(b);
        }

        public HgTupleStream getStream() {
            return HgTupleStream.this;
        }

//        @Override
//        public boolean equals(Object o) {
//            if (o instanceof HgTuple) {
//                HgTuple ot = (HgTuple)o;
//                if (ot._entries.size() == _entries.size() &&
//                        _containedTypes.keySet().containsAll(ot.getStream()._containedTypes.keySet())) {
//                    for (TableID<?> myType: _containedTypes.keySet()) {
//                        if (!get(myType).equals(ot.get(myType))) {
//                           return false;
//                        }
//                    }
//                    return true;
//                }
//            }
//            return false;
//        }

        private void insertRecords(HgTuple t) {
            for (TableID<?> id : t.getStream()._containedTypes.keySet()) {
                int index = _containedTypes.get(id);
                if (index < _entries.size() && _entries.get(index) != null) {
                    throw new IllegalArgumentException("Cannot merge tuples which contain the same ids");
                }
                insertRecord(id, t.get(id));
            }
        }

        private void insertRecord(TableID<?> s, Object o) {
            // fetch the tuple index from the contained types with the given id
            int tupleIndex = _containedTypes.get(s);

            // ensure we have the capacity
            _entries.ensureCapacity(tupleIndex + 1);
            while (_entries.size() < tupleIndex + 1) {
                _entries.add(null);
            }

            _entries.set(tupleIndex, o);
        }

        @SuppressWarnings("unchecked") // cast to T
        public <T> T get(TableID<T> id) {
            if (!_containedTypes.containsKey(id)) {
                throw new IllegalArgumentException("ID not present in HgTupleStream instance.");
            }
            return (T) _entries.get(_containedTypes.get(id));
        }

        public Object extractJoinedField() {
            return _fwdFE.extractValue(this.get(_fwdFE.getTableId()));
        }

        public Object extractJoinedEntry() {
            return get(_fwdFE.getTableId());
        }

        public String toString() {
            return _entries.toString();
        }
    }
}
