package org.mercurydb.queryutils;

import org.mercurydb.queryutils.HgTupleStream;

public class HgWrappedTupleStream extends HgTupleStream {

    private final HgTupleStream _fwdStream;

    public HgWrappedTupleStream(HgTupleStream stream) {
        super(stream, stream.getContainedIds());
        this._fwdStream = stream;
    }

    @Override
    public boolean isIndexed() {
        return _fwdStream.isIndexed();
    }

    @Override
    public void reset() {
        _fwdStream.reset();
    }

    @Override
    public boolean hasNext() {
        return _fwdStream.hasNext();
    }

    @Override
    public HgTuple next() {
        return _fwdStream.next();
    }
}
