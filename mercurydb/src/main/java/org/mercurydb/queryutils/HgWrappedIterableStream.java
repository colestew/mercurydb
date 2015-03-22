package org.mercurydb.queryutils;

import com.google.common.collect.Iterables;

import java.util.Iterator;

abstract public class HgWrappedIterableStream<T> extends HgStream<T> {

    private Iterable<T> streamSeed;
    private Iterator<T> stream;

    public HgWrappedIterableStream(Iterable<T> streamSeed) {
        this.streamSeed = streamSeed;
        stream = streamSeed.iterator();
    }

    public HgWrappedIterableStream<T> concat(HgWrappedIterableStream<? extends T> or) {
        streamSeed = Iterables.concat(streamSeed, or.streamSeed);
        stream = streamSeed.iterator();
        return this;
    }

    public Iterable<T> getStreamSeed() {
        return streamSeed;
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
