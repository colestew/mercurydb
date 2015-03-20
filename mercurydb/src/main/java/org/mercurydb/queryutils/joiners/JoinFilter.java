package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgTupleStream;
import org.mercurydb.queryutils.JoinPredicate;


/**
 * Performs a filter join on two streams
 * where one's set of contained types is
 * a subset of the other's.
 */
public class JoinFilter extends HgPolyTupleStream {

    private final HgTupleStream ap;
    private final HgTupleStream bp;
    private HgTuple currA;

    public JoinFilter(JoinPredicate predicate) {
        super(predicate);

        // Perform Filter operation on A
        if (predicate.streamA.getContainedIds().retainAll(predicate.streamB.getContainedIds())) {
            ap = predicate.streamA;
            bp = predicate.streamB;
        } else {
            ap = predicate.streamB;
            bp = predicate.streamA;
        }
    }

    @Override
    public boolean hasNext() {
        if (ap.hasNext()) {
            currA = ap.next();
            Object jkv1o = ap.extractFieldFromTuple(currA);
            Object jkv2o = bp.extractFieldFromTuple(currA);

            // TODO why does having these be equal mean hasNext() is true?
            // TODO also if this logic is sound, reduce it into a single expression as IntelliJ recommends
            if (_predicate.relation.test(jkv1o, jkv2o)) {
                return true;
            } else {
                return hasNext();
            }
        }

        return false;
    }

    @Override
    public HgTuple next() {
        return currA;
    }

    @Override
    public void reset() {
        super.reset();
        currA = null;
    }
}
