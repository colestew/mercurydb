package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgRelation;
import org.mercurydb.queryutils.HgTupleStream;
import org.mercurydb.queryutils.JoinPredicate;

import java.util.Collections;
import java.util.Iterator;


/**
 * Joins two JoinStreams, one of which must
 * be indexed. Scans over the non-indexed stream
 * and uses the index on the indexed stream.
 */
public class JoinIndexScan extends HgPolyTupleStream {

    private final HgTupleStream ap;
    private final HgTupleStream bp;

    private Object currB;
    private Iterator<Object> bInstances;
    private Iterator<Object> aInstances;

    public JoinIndexScan(JoinPredicate pred) {
        super(pred);

        if (!pred.relation.equals(HgRelation.EQ)) {
            throw new IllegalArgumentException("Relations other than == are not supported for indices");
        }

        if (pred.streamA.isIndexed()) {
            ap = pred.streamB;
            bp = pred.streamA;
        } else if (pred.streamA.isIndexed()){
            ap = pred.streamA;
            bp = pred.streamB;
        } else {
            throw new IllegalArgumentException("One of the arguments must be indexed!");
        }
    }

    private void setup() {
        bInstances = bp.getObjectIterator();
        aInstances = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        if (aInstances.hasNext()) {
            return true;
        } else {
            while (bInstances.hasNext()) {
                currB = bInstances.next();
                Object currKey = bp.extractField(currB);
                Iterable<Object> aIterable = ap.getIndex().get(currKey);
                if (aIterable != null) {
                    aInstances = aIterable.iterator();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public HgTuple next() {
        return new HgTuple(ap.getContainerId(), aInstances.next(), bp.getContainerId(), currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }
}
