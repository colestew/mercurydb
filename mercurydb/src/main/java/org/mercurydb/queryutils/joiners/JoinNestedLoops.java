package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgTupleStream;

/**
 * Simple nested loops join algorithm.
 */
public class JoinNestedLoops extends HgPolyTupleStream {
    private final HgTupleStream a;
    private final HgTupleStream b;

    private HgTuple currA;
    private HgTuple currB;

    public JoinNestedLoops(HgTupleStream a, HgTupleStream b) {
        super(a, b);
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean hasNext() {
        while (b.hasNext() && currA != null) {
            currB = b.next();
            if (a.extractFieldFromTuple(currA)
                    .equals(b.extractFieldFromTuple(currB))) {
                return true;
            }
        }

        if (a.hasNext()) {
            b.reset();
            currA = a.next();
            return hasNext();
        }

        return false;
    }

    @Override
    public HgTuple next() {
        // TODO investigate possibly different constructor in HgTuple
        return new HgTuple(a.getContainerId(), currA.get(a.getContainerId()), b.getContainerId(), b.getContainerId());
    }

    @Override
    public void reset() {
        super.reset();
        currA = null;
        currB = null;
    }
}
