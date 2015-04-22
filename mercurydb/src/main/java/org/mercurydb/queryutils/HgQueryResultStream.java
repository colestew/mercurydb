package org.mercurydb.queryutils;

public class HgQueryResultStream<T> extends HgWrappedIterableStream<T> {
    public HgQueryResultStream(Iterable<T> streamSeed) {
        super(streamSeed);
    }

    @Override
    public HgTupleStream joinOn(ValueExtractable fe) {
        return HgTupleStream.createJoinInput(fe, this, true);
    }
}
