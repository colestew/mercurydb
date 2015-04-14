package org.mercurydb.queryutils;

import org.mercurydb.queryutils.joiners.JoinFilter;
import org.mercurydb.queryutils.joiners.JoinIndexScan;
import org.mercurydb.queryutils.joiners.JoinNestedLoops;
import org.mercurydb.queryutils.joiners.JoinTempIndexScan;

import java.util.*;

/**
 * <p>
 * This class is the main class used for queries and join operations
 * on a hgdb instance. It can be used like the following:
 * </p>
 * TODO UPDATE DOCUMENTATION
 */
public class HgDB {
    private static final Comparator<AbstractFieldExtractablePredicate<?, ?>> QUERY_COMPARATOR =
            (a, b) -> {
                int aPriority = getQueryPredicatePriority(a);
                int bPriority = getQueryPredicatePriority(b);
                return aPriority - bPriority;
            };

    private static int getQueryPredicatePriority(AbstractFieldExtractablePredicate<?, ?> predicate) {
        if (predicate instanceof FieldExtractableRelation) {
            FieldExtractableRelation<?, ?> fer = (FieldExtractableRelation<?, ?>) predicate;
            if (fer.isIndexed() && fer.relation == HgRelation.EQ) {
                return -(fer.getIndex().get(fer.value).size());
            } else if (isIndexCompatible(fer.getIndex(), fer.relation)) {
                return 2;
            }
        }

        return 3;
    }

    private static final Comparator<JoinPredicate> JOIN_PREDICATE_COMPARATOR =
            (a, b) -> {
                int aPriority = getJoinPredicatePriority(a);
                int bPriority = getJoinPredicatePriority(b);
                return aPriority - bPriority;
            };

    private static int getJoinPredicatePriority(JoinPredicate predicate) {
        if (isStreamAndIndexCompatible(predicate.streamA, predicate.relation) ||
                isStreamAndIndexCompatible(predicate.streamB, predicate.relation)) {
            return predicate.relation == HgRelation.EQ ? 1 : 2;
        }

        return predicate.relation == HgRelation.EQ ? 3 : 4;
    }

    @SuppressWarnings("unchecked") // cast from Iterable<Object> to Iterable<T>
    public static <T> HgStream<T> query(AbstractFieldExtractablePredicate<T, ?>... extractableValues) {
        if (extractableValues.length == 0) {
            return new HgRetrievalStream<>(Collections.<T>emptyList());
        }

        Arrays.sort(extractableValues, QUERY_COMPARATOR);

        FieldExtractableSeed<T> fe = extractableValues[0];
        HgStream<T> stream = fe.getDefaultStream();

        int start = 0;
        if (fe instanceof FieldExtractableRelation) {
            FieldExtractableRelation<T, ?> fer = (FieldExtractableRelation<T, ?>) fe;
            if (isIndexCompatible(fer.getIndex(), fer.relation)) {
                start = 1;
                HgRelation hgRelation = (HgRelation) fer.relation;
                Iterable<Object> iter = hgRelation.getFromIndex(fer.getIndex(), fer.value);
                stream = new HgQueryResultStream<>((Iterable<T>) iter);
            }
        }

        for (int i = start; i < extractableValues.length; ++i) {
            stream = stream.filter(extractableValues[i]);
        }

        return stream;
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
        HgPolyTupleStream result = join(preds[0]);

        if (preds.length == 1) {
            return result;
        }

        Arrays.sort(preds, JOIN_PREDICATE_COMPARATOR);

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
            HgBiPredicate<?, ?> relation) {
        return join(new JoinPredicate(a, b, relation));
    }

    public static HgPolyTupleStream join(JoinPredicate predicate) {
        HgTupleStream a = predicate.streamA, b = predicate.streamB;

        if (!a.getContainedIds().retainAll(b.getContainedIds())
                || !b.getContainedIds().retainAll(a.getContainedIds())) {
            /*
             * Filter operation
             */
            return new JoinFilter(predicate);
        } else if (isStreamAndIndexCompatible(a, predicate.relation) ||
                isStreamAndIndexCompatible(b, predicate.relation)) {

            // TODO decide if IndexIntersection is actually more efficient in any case!
//            if (a.isIndexed() && b.isIndexed()) {
//
//                /*
//                 * Both A and B indexed
//                 * Do index intersection A, B
//                 */
//                return new JoinIndexIntersection(predicate);
//            } else {

                /*
                 * Only A indexed
                 * Scan B, use A index
                 */
            return new JoinIndexScan(predicate);
        } else if (predicate.relation instanceof HgRelation) {
            /*
             * Neither is indexed and relation is known
             * Do hash join
             */
            return new JoinTempIndexScan(predicate);
        } else {
            /*
             * Neither is indexed and relation is unknown.
             * Time for some nested loops.
             *
             * ¯\_(ツ)_/¯
             */
            return new JoinNestedLoops(predicate);
        }
    }

    public static boolean isStreamAndIndexCompatible(HgTupleStream o, HgBiPredicate<?, ?> pred) {
        return o.isIndexed() && isIndexCompatible(o.getIndex(), pred);
    }

    public static boolean isIndexCompatible(Map<?, ?> index, HgBiPredicate<?, ?> pred) {
        boolean indexIsTreeMap = index instanceof TreeMap<?, ?>;
        boolean predIsHgRelation = pred instanceof HgRelation;

        return index != null && (pred == HgRelation.EQ || pred == HgRelation.NE || (indexIsTreeMap && predIsHgRelation));
    }
}
