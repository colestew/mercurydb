package org.mercurydb.queryutils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class HgTupleMap implements Map<HgTupleStream.HgTuple, Set<Object>> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Set<Object> get(Object key) {
        return null;
    }

    @Override
    public Set<Object> put(HgTupleStream.HgTuple key, Set<Object> value) {
        return null;
    }

    @Override
    public Set<Object> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends HgTupleStream.HgTuple, ? extends Set<Object>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<HgTupleStream.HgTuple> keySet() {
        return null;
    }

    @Override
    public Collection<Set<Object>> values() {
        return null;
    }

    @Override
    public Set<Entry<HgTupleStream.HgTuple, Set<Object>>> entrySet() {
        return null;
    }
}
