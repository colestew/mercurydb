package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.*;

import java.util.Collections;
import java.util.Iterator;


/**
 * Joins two JoinStreams, one of which must
 * be indexed. Scans over the non-indexed stream
 * and uses the index on the indexed stream.
 */
public class JoinIndexScan extends HgPolyTupleStream {

    protected final HgTupleStream ap;
    protected final HgTupleStream bp;
    protected final HgRelation relation;

    protected HgTuple currB;
    private Iterator<HgTuple> bInstances;
    protected Iterator<Object> aInstances;


    private static JoinPredicate fixAndCheckPredicate(JoinPredicate predicate) {
        if (!(HgDB.isStreamAndIndexCompatible(predicate.streamA, predicate.relation)) &&
                !(HgDB.isStreamAndIndexCompatible(predicate.streamB, predicate.relation))){
            throw new IllegalArgumentException("None of the given streams' indexes are compatible with the relation!");
        }

        return HgDB.isStreamAndIndexCompatible(predicate.streamA, predicate.relation) ?
                predicate : predicate.swapLhsAndRhs();
    }

    public JoinIndexScan(JoinPredicate pred) {
        super(fixAndCheckPredicate(pred));

        ap = _predicate.streamA;
        bp = _predicate.streamB;
        relation = (HgRelation)_predicate.relation;
    }

    private void setup() {
        bInstances = bp.iterator();
        aInstances = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        if (aInstances.hasNext()) {
            return true;
        } else {
            while (bInstances.hasNext()) {
                currB = bInstances.next();
                Object currKey = bp.extractFieldFromTuple(currB);
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
        return new HgTuple(ap.getTableId(), aInstances.next(), currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }
}
