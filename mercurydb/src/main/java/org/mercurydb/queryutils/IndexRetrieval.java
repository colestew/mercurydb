package org.mercurydb.queryutils;

import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * // TODO documentation
 * @param <T> // TODO documentation
 */
public class IndexRetrieval<T> extends HgStream<T> {
    private final Map<Object, Set<T>> index;

    @SuppressWarnings("unchecked")
    public IndexRetrieval(Map<?, Set<T>> index) {
        super(Sets.newHashSet(index.values()).size());

        this.index = (Map<Object, Set<T>>) index;
    }

    public Iterable<T> get(Object o) {
        return index.get(o);
    }

    public Iterable<Object> keys() {
        return new Iterable<Object>() {
            @Override
            public Iterator<Object> iterator() {
                return index.keySet().iterator();
            }
        };
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T next() {
        throw new UnsupportedOperationException();
    }
}
