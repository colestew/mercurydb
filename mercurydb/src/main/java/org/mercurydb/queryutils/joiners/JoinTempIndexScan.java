package org.mercurydb.queryutils.joiners;

import org.mercurydb.queryutils.HgWrappedTupleStream;
import org.mercurydb.queryutils.HgRelation;
import org.mercurydb.queryutils.HgTupleStream;
import org.mercurydb.queryutils.JoinPredicate;

import java.util.*;

public class JoinTempIndexScan extends JoinIndexScan {

    private static JoinPredicate createIndexedPredicate(JoinPredicate predicate) {
        final Map<Object, Set<Object>> aMap;
        if (predicate.relation == HgRelation.EQ || predicate.relation == HgRelation.NE) {
            aMap = new HashMap<>();
        } else {
            aMap = new TreeMap<>();
        }

        // Inhale stream A into hash table
        for (HgTupleStream.HgTuple aInstance : predicate.streamA) {
            Object key = aInstance.extractJoinedField();

            Set<Object> l = aMap.get(key);
            if (l == null) {
                l = new HashSet<Object>();
            }

            l.add(aInstance);
            aMap.put(key, l);
        }

        HgTupleStream aIndexed = new HgWrappedTupleStream(predicate.streamA) {
            @Override
            public boolean isIndexed() {
                return true;
            }

            @Override
            public Map<Object, Set<Object>> getIndex() {
                return aMap;
            }
        };
        return new JoinPredicate(aIndexed, predicate.streamB, predicate.relation);
    }


    public JoinTempIndexScan(JoinPredicate pred) {
        super(createIndexedPredicate(pred));
    }

    @Override
    public HgTuple next() {
       return new HgTuple((HgTuple)aInstances.next(), currB);
    }
}
