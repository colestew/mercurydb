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
public class HgRetrievalStream<T> extends HgWrappedIterableStream<T> {

    public HgRetrievalStream(Iterable<T> streamSeed) {
        super(streamSeed);
    }

    @Override
    public HgTupleStream joinOn(FieldExtractable fe) {
        return HgTupleStream.createJoinInput(fe, this, false);
    }
}
