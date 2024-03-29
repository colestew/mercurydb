package org.mercurydb.queryutils;

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
    public HgTupleStream joinOn(ValueExtractable fe) {
        return HgTupleStream.createJoinInput(fe, this, false);
    }
}
