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
public class JoinPredicate implements Comparable<JoinPredicate> {
	HgJoinInput stream1, stream2;
	public final HgRelation predicate;

    /**
     * Construct a JoinPredicate from two HgMonoStreams.
     * @param s1 The first stream to join on.
     * @param s2 The second stream to join on.
     */
     	public JoinPredicate(HgJoinInput s1, HgJoinInput s2) {
		this(HgRelation.EQ, s1, s2);
    }

    /**
     * Construct a JoinPredicate from two HgMonoStreams with a memo about what the predicate represents.
     * @param relation A short memo about the relation represented by this JoinPredicate.
     * @param s1 The first stream to join on.
     * @param s2 The second stream to join on.
     */
     	public JoinPredicate(HgRelation predicate, HgJoinInput s1, HgJoinInput s2) {
		this.predicate = predicate;
        this.stream1 = s1;
        this.stream2 = s2;
    }

    /**
     * Counts the total number of indices available on this JoinPredicate's streams.
     *
     * @return The number of indices.
     */
    private int numIndices() {
        int res = 0;
		if (stream1.isIndexed()) ++res;
		if (stream2.isIndexed()) ++res;
        return res;
    }

    /**
     * Retrieve the total cardinality of the input streams.
     * // TODO is this what we really want to do?
     *
     * @return The cardinality.
     */
    private int cardinality() {
		return stream1.getCardinality() + stream2.getCardinality();
    }

    /**
     * <p>
     * This method compares JoinPredicates with the
     * following ordering priority:
     * </p>
     *
     * <ol>
     * <li>Number of indices (greater first, i.e. descending)</li>
     * <li>Cardinality of its streams (smaller first, i.e. ascending [natural])</li>
     * </ol>
     *
     * @param o // TODO documentation
     * @return // TODO documentation
     */
    @Override
    public int compareTo(JoinPredicate o) {
        int idxDiff = o.numIndices() - this.numIndices();
        if (idxDiff == 0) {
            return this.cardinality() - o.cardinality();
        } else {
            return idxDiff;
        }
    }
}
