package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgRelation;
import org.mercurydb.queryutils.JoinPredicate;

import java.util.*;

/**
 * Joins two JoinStreams. Both of which must be
 * indexed. Performs an intersection of two JoinStreams,
 * both of which must be index retrievals.
 */
public class JoinIndexIntersection extends HgPolyTupleStream {

    private Iterator<Object> bKeys;
    private Iterator<Object> aInstances;
    private Object currA;
    private Iterator<Object> bInstances;
    private Object currB;
    private Iterable<Object> aSeed;

    private HgRelation relation;

    public JoinIndexIntersection(JoinPredicate pred) {
        super(pred);

        if (pred.relation instanceof HgRelation) {
            relation = (HgRelation)pred.relation;
        } else {
            throw new IllegalArgumentException("Relation must be an HgRelation to use index!");
        }
        if (!pred.streamA.isIndexed() || !pred.streamB.isIndexed()) {
            throw new IllegalArgumentException("Both inputs must be indexed!");
        }
        setup();
    }

    private void setup() {
        bKeys = _predicate.streamB.getIndex().keySet().iterator();
        currA = null;
        currB = null;
        aSeed = null;
        aInstances = Collections.emptyIterator();
        bInstances = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        // Note: this was difficult for Cole's feeble mind to think about
        if (aInstances.hasNext()) {
            currA = aInstances.next();
            return true;
        } else if (bInstances.hasNext()) {
            currB = bInstances.next();
            aInstances = aSeed.iterator();
            return hasNext();
        }

        // While there are more keys in b
        while (bKeys.hasNext()) {
            // fetch the next key | field value from which to retrieve a's and b's
            Object currKey = bKeys.next();

            // try and fetch an a from a's index
            aSeed = relation.getFromIndex(_predicate.streamA.getIndex(), currKey);

            if (aSeed != null) {
                // if we found an a, fetch b's instances at this point
                bInstances = _predicate.streamB.getIndex().get(currKey).iterator();

                // advance the iterator. The if statement at the top should catch now.
                return hasNext();
            }
        }

        return false;
    }

    @Override
    public HgTuple next() {
        return this.new HgTuple(
                _predicate.streamA.getTableId(),
                currA,
                _predicate.streamB.getTableId(),
                currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }

}
