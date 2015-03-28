package org.mercurydb.queryutils;

/**
 * JoinDriver join methods always return these.
 */
public abstract class HgPolyTupleStream extends HgTupleStream {
    protected final JoinPredicate _predicate;

    public HgPolyTupleStream(JoinPredicate predicate) {
        super(predicate.streamA.getContainedIds(), predicate.streamB.getContainedIds());
        this._predicate = predicate;
    }

    @Override
    public boolean isIndexed() {
        return false;
    }

    @Override
    public void reset() {
        _predicate.streamA.reset();
        _predicate.streamB.reset();
    }
}
