package org.mercurydb.queryutils;

import com.google.common.collect.Iterables;

import java.util.Iterator;

/**
 * Table classes will define these. Each
 * instance has two possible implementations switched
 * by the isIndexed() method. If it is indexed, get(F i),
 * hasNextKey(), and nextKey() are defined.
 *
 * @param <T> The ClassType
 */
public class Retrieval<T> extends HgStream<T> {
    private Iterable<T> streamSeed;
    private Iterator<T> stream;

    public Retrieval(Iterable<T> streamSeed, int cardinality) {
        super(cardinality);
        this.streamSeed = streamSeed;
        stream = streamSeed.iterator();
    }

    public Retrieval<T> join(Retrieval<? extends T> or) {
        streamSeed = Iterables.concat(streamSeed, or.streamSeed);
        super.cardinality += or.cardinality;
        return this;
    }

    @Override
    public boolean hasNext() {
        return stream.hasNext();
    }

    @Override
    public T next() {
        return stream.next();
    }

    @Override
    public void reset() {
        stream = streamSeed.iterator();
    }
}
