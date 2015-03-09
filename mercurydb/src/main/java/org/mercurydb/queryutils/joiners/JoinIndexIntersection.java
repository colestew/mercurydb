package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgPolyTupleStream;
import org.mercurydb.queryutils.HgTuple;
import org.mercurydb.queryutils.HgTupleStream;

import java.util.Collections;
import java.util.Iterator;

/**
 * Joins two JoinStreams. Both of which must be
 * indexed. Performs an intersection of two JoinStreams,
 * both of which must be index retrievals.
 */
public class JoinIndexIntersection extends HgPolyTupleStream {

    private final HgTupleStream a;
    private final HgTupleStream b;
    private Iterator<Object> aKeys;
    private Iterator<Object> aInstances;
    private Object currA;
    private Iterator<Object> bInstances;
    private Object currB;
    private Iterable<Object> bSeed;

    public JoinIndexIntersection(HgTupleStream a, HgTupleStream b) {
        super(a, b);
        if (!a.isIndexed() || !b.isIndexed()) {
            throw new IllegalArgumentException("Both inputs must be indexed!");
        }
        this.a = a;
        this.b = b;
        setup();
    }

    private void setup() {
        aKeys = a.getIndex().keySet().iterator();
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
            bSeed = b.getIndex().get(currKey);
            if (bSeed != null) {
                // if we found a b, fetch a's instances at this point
                aInstances = a.getIndex().get(currKey).iterator();

                // advance the iterator. The if statement at the top should catch now.
                return hasNext();
            }
        }
        return false;
    }

    @Override
    public HgTuple next() {
        return new HgTuple(a, currA, b, currB);
    }

    @Override
    public void reset() {
        super.reset();
        setup();
    }

}
