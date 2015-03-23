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
    private final HgRelation relation;

    private Object currB;
    private Iterator<Object> bInstances;
    private Iterator<Object> aInstances;


    private static JoinPredicate fixAndCheckPredicate(JoinPredicate predicate) {
        if (!(predicate.relation instanceof HgRelation)) {
            throw new IllegalArgumentException("Relation must be an HgRelation to use index!");
        }

        if (predicate.numIndices() == 0) {
            throw new IllegalArgumentException("One of the arguments must be indexed!");
        }

        return predicate.streamA.isIndexed() ? predicate : predicate.swapLhsAndRhs();
    }

    public JoinIndexScan(JoinPredicate pred) {
        super(fixAndCheckPredicate(pred));

        ap = _predicate.streamA;
        bp = _predicate.streamB;
        relation = (HgRelation)_predicate.relation;
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
                Iterable<Object> aIterable = relation.getFromIndex(ap.getIndex(), currKey);
                if (aIterable != null) {
                    aInstances = aIterable.iterator();
                    return hasNext();
                }
            }
        }

        return false;
    }

    @Override
    public HgTuple next() {
        return new HgTuple(ap.getTableId(), aInstances.next(), bp.getTableId(), currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }
}
