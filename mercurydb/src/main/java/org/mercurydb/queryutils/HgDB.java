package org.mercurydb.queryutils;

import org.mercurydb.queryutils.joiners.JoinFilter;
import org.mercurydb.queryutils.joiners.JoinIndexIntersection;
import org.mercurydb.queryutils.joiners.JoinIndexScan;
import org.mercurydb.queryutils.joiners.JoinNestedLoops;

import java.util.*;

/**
 * <p/>
 * This class is the main class used for queries and join operations
 * on a hgdb instance. It can be used like the following:
 * <p/>
 * TODO UPDATE DOCUMENTATION
 */
public class HgDB {
    @SafeVarargs
    public static <T> HgStream<T> query(AbstractFieldExtractablePredicate<T,?>... extractableValues) {
        if (extractableValues.length == 0) {
            throw new IllegalArgumentException("Must supply at least one argument to query");
        }

        // find smallest applicable index
        int smallestIndex = Integer.MAX_VALUE;
        Set<Object> index = null;
        FieldExtractable usedFE = null;
        for (FieldExtractableSeed<T> fe : extractableValues) {
            if (fe instanceof FieldExtractableRelation) {
                FieldExtractableRelation<T,?> fer = (FieldExtractableRelation<T,?>) fe;
                if (fer.isIndexed() && fer.relation == HgRelation.EQ) {
                    Set<Object> newIndex = fe.getIndex().get(fer.value);
                    int newSmallestIndex = newIndex.size();
                    if (newSmallestIndex < smallestIndex) {
                        smallestIndex = newSmallestIndex;
                        index = newIndex;
                        usedFE = fe;
                    }
                }
            }
        }

        HgStream<T> seed;

        if (index != null) {
            seed = new Retrieval(index, index.size());
        } else {
            seed = extractableValues[0].getDefaultStream();
        }

        for (AbstractFieldExtractablePredicate<T,?> fe : extractableValues) {
            if (fe == usedFE) continue;
            seed = seed.filter(fe);
        }

        return seed;
    }


    /**
     * Joins a set of Predicates. Creates optimal
     * join operation according to those rules defined
     * in JoinPredicate.
     *
     * @param preds One or more JoinPredicates
     * @return a JoinResult of a join on preds
     * @throws IllegalStateException if preds do not unify
     */
    public static HgPolyTupleStream join(JoinPredicate... preds) {
        if (preds.length == 1) {
            return join(preds[0]);
        } else if (preds.length > 1) {
            Arrays.sort(preds);
            HgPolyTupleStream result = join(preds[0]);

            for (int i = 1; i < preds.length; ++i) {
                JoinPredicate p = preds[i];
                if (result.containsId(p.streamA.getTableId())) {
                    preds[i] = new JoinPredicate(result.joinOn(p.streamA), p.streamB, p.relation);
                } else if (result.containsId(p.streamB.getTableId())) {
                    preds[i] = new JoinPredicate(p.streamA, result.joinOn(p.streamB), p.relation);
                } else {
                    continue;
                }

                return join(Arrays.copyOfRange(preds, 1, preds.length));
            }

            throw new IllegalStateException("Predicates do not unify!");
        } else {
            throw new IllegalArgumentException("Must supply at least one predicate to join.");
        }
    }

    /**
     * Returns a HgPolyTupleStream using an equality predicate.
     *
     * @param a HgTupleStream // TODO documentation
     * @param b HgTupleStream // TODO documentation
     * @return The HgPolyTupleStream resulting from performing on a join on the inputs with the equality relation.
     */
    public static HgPolyTupleStream join(
            final HgTupleStream a,
            final HgTupleStream b) {
        return join(a, b, HgRelation.EQ);
    }

    /**
     * Joins two JoinStreams and returns a JoinResult. A JoinResult
     * is a JoinStream which is basically an iterator over JoinRecords.
     * JoinRecords are essentially a Map of class types to Objects. So you
     * can select Objects from the result based on their class type. It
     * will select the correct join method based on the index status
     * of its arguments.
     *
     * @param relation HgRelation // TODO documentation
     * @param a        HgTupleStream // TODO documentation
     * @param b        HgTupleStream // TODO documentation
     * @return The HgPolyTupleStream resulting from performing on a join on the inputs with the given relation.
     */
    public static HgPolyTupleStream join(
            HgTupleStream a,
            HgTupleStream b,
            HgRelation relation) {
        return join(new JoinPredicate(a, b, relation));
    }

    public static HgPolyTupleStream join(JoinPredicate predicate) {
        HgTupleStream a = predicate.streamA, b = predicate.streamB;

        if (predicate.relation != HgRelation.EQ) {
            return new JoinNestedLoops(predicate);
        } else if (!a.getContainedIds().retainAll(b.getContainedIds())
                || !b.getContainedIds().retainAll(a.getContainedIds())) {
            /*
             * Filter operation
             */
            return new JoinFilter(predicate);
        } else if (a.isIndexed() && b.isIndexed()) {
            /*
             * Both A and B indexed
             * Do index intersection A, B
             */
            return new JoinIndexIntersection(predicate);
        } else if (a.isIndexed() || b.isIndexed()) {
            /*
             * Only A indexed
             * Scan B, use A index
             */
            return new JoinIndexScan(predicate);
        } else {
            /*
             * Neither is indexed
             * Do hash join
             */
            return joinHash(a, b);
        }
    }

    /**
     * Simple hash join algorithm. Inhales the results
     * into a // TODO finish documentation
     *
     * @param a // TODO documentation
     * @param b // TODO documentation
     * @return // TODO documentation
     */
    @SuppressWarnings("unchecked")
    public static HgPolyTupleStream joinHash(
            final HgTupleStream a,
            final HgTupleStream b) {

        final HgTupleStream ap;
        final HgTupleStream bp;

        if (a.getCardinality() < b.getCardinality()) {
            ap = a;
            bp = b;
        } else {
            ap = b;
            bp = a;
        }

        final Map<Object, Set<Object>> aMap = new HashMap<>();

        // Inhale stream A into hash table
        for (HgTupleStream.HgTuple aInstance : ap) {
            Object key = aInstance.extractJoinedField();

            Set<Object> l = aMap.get(key);
            if (l == null) {
                l = new HashSet<>();
            }

            l.add(aInstance);
            aMap.put(key, l);
        }

        FieldExtractableFakeIndex fei = new FieldExtractableFakeIndex(ap.getFieldExtractor(), aMap);
        ap.setJoinKey(fei);

        return new JoinIndexScan(new JoinPredicate(ap, bp));
    }
}
