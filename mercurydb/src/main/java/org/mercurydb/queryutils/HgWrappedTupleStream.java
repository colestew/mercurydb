package org.mercurydb.queryutils;

abstract public class HgWrappedTupleStream extends HgTupleStream {

    private final HgTupleStream _stream;

    public HgWrappedTupleStream(HgTupleStream stream) {
        super(stream._fwdFE);
        _stream = stream;
    }

    @Override
    public void reset() {
        _stream.reset();
    }

    @Override
    public boolean hasNext() {
        return _stream.hasNext();
    }

    @Override
    public HgTuple next() {
        return _stream.next();
    }
}
