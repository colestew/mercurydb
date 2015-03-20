package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.JoinPredicate;

/**
 * Simple nested loops join algorithm.
 */
public class JoinNestedLoops extends HgPolyTupleStream {
    private HgTuple currA;
    private HgTuple currB;

    public JoinNestedLoops(JoinPredicate predicate) {
        super(predicate);
    }

    @Override
    public boolean hasNext() {
        while (_predicate.streamB.hasNext() && currA != null) {
            currB = _predicate.streamB.next();
            if (_predicate.relation.test(
                    _predicate.streamA.extractFieldFromTuple(currA),
                    _predicate.streamB.extractFieldFromTuple(currB))) {
                return true;
            }
        }

        if (_predicate.streamA.hasNext()) {
            _predicate.streamB.reset();
            currA = _predicate.streamA.next();
            return hasNext();
        }

        return false;
    }

    @Override
    public HgTuple next() {
        return new HgTuple(currA, currB);
    }

    @Override
    public void reset() {
        super.reset();
        currA = null;
        currB = null;
    }
}
