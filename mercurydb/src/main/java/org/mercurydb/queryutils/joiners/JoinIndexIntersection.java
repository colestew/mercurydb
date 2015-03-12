package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgRelation;
import org.mercurydb.queryutils.JoinPredicate;

import java.util.Collections;
import java.util.Iterator;

/**
 * Joins two JoinStreams. Both of which must be
 * indexed. Performs an intersection of two JoinStreams,
 * both of which must be index retrievals.
 */
public class JoinIndexIntersection extends HgPolyTupleStream {

    private Iterator<Object> aKeys;
    private Iterator<Object> aInstances;
    private Object currA;
    private Iterator<Object> bInstances;
    private Object currB;
    private Iterable<Object> bSeed;

    public JoinIndexIntersection(JoinPredicate pred) {
        super(pred);
        if (!pred.relation.equals(HgRelation.EQ)) {
            throw new IllegalArgumentException("Relations other than == are not supported for indices");
        }
        if (!pred.streamA.isIndexed() || !pred.streamB.isIndexed()) {
            throw new IllegalArgumentException("Both inputs must be indexed!");
        }
        setup();
    }

    private void setup() {
        aKeys = _predicate.streamA.getIndex().keySet().iterator();
        currA = null;
        currB = null;
        bSeed = null;
        aInstances = Collections.emptyIterator();
        bInstances = Collections.emptyIterator();
    }


    @Override
    public boolean hasNext() {
        // Note: this was difficult for Cole's feeble mind to think about
        // TODO: comment this sorcery
        if (bInstances.hasNext()) {
            currB = bInstances.next();
            return true;
        } else if (aInstances.hasNext()) {
            currA = aInstances.next();
            bInstances = bSeed.iterator();
            return hasNext();
        }

        // While there are more keys in a
        while (aKeys.hasNext()) {
            // fetch the next key | field value from which to retrieve a's and b's
            Object currKey = aKeys.next();
            // try and fetch a b from b's index
            bSeed = _predicate.streamB.getIndex().get(currKey);
            if (bSeed != null) {
                // if we found a b, fetch a's instances at this point
                aInstances = _predicate.streamA.getIndex().get(currKey).iterator();

                // advance the iterator. The if statement at the top should catch now.
                return hasNext();
            }
        }
        return false;
    }

    @Override
    public HgTuple next() {
        return this.new HgTuple(
                _predicate.streamA.getContainerId(),
                currA,
                _predicate.streamB.getContainerId(),
                currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }

}
