package org.mercurydb.queryutils;

/**
 * Simple class for JoinPredicates. This is used
 * for Joins like in the following statement:
 * <p>
 * * For >1 predicates -- A.X=B.Y and B.C=C.D and A.Y=D.F
 * JoinDriver.join(
 * new Predicate(TableA.joinX(), TableB.joinY()),
 * new Predicate(TableB.joinC(), TableC.joinD()),
 * new Predicate(TableA.joinY(), TableD.joinF()));
 */
public class JoinPredicate {
    public final HgTupleStream streamA, streamB;
    public final HgBiPredicate relation;

    /**
     * Construct a JoinPredicate from two HgMonoStreams.
     *
     * @param s1 The first stream to join on.
     * @param s2 The second stream to join on.
     */
    public JoinPredicate(HgTupleStream s1, HgTupleStream s2) {
        this(s1, s2, HgRelation.EQ);
    }

    /**
     * Construct a JoinPredicate from two HgMonoStreams with a memo about what the predicate represents.
     *
     * @param s1        The first stream to join on.
     * @param s2        The second stream to join on.
     * @param relation  A short memo about the relation represented by this JoinPredicate.
     */
    public JoinPredicate(HgTupleStream s1, HgTupleStream s2, HgBiPredicate<?,?> relation) {
        this.relation = relation;
        this.streamA = s1;
        this.streamB = s2;
    }

    public JoinPredicate swapLhsAndRhs() {
        if (relation instanceof HgRelation) {
            return new JoinPredicate(
                    streamB,
                    streamA,
                    ((HgRelation) relation).reversedRelation());
        }

        throw new IllegalArgumentException("Cannot reverse relation for predicates that are not HgRelations.");
    }

    /**
     * Counts the total number of indices available on this JoinPredicate's streams.
     *
     * @return The number of indices.
     */
    public int numIndices() {
        int res = 0;
        if (streamA.isIndexed()) ++res;
        if (streamB.isIndexed()) ++res;
        return res;
    }
}
