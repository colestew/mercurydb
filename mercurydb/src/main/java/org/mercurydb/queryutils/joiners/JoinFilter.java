package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgTupleStream;


/**
 * Performs a filter join on two streams
 * where one's set of contained types is
 * a subset of the other's.
 */
public class JoinFilter extends HgPolyTupleStream {

    private final HgTupleStream ap;
    private final HgTupleStream bp;
    private HgTuple currA;

    public JoinFilter(HgTupleStream a, HgTupleStream b) {
        super(a, b);

        // Perform Filter operation on A
        if (b.getContainedIds().retainAll(a.getContainedIds())) {
            ap = a;
            bp = b;
        } else {
            ap = b;
            bp = b;
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
            if (jkv1o.equals(jkv2o)) {
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
