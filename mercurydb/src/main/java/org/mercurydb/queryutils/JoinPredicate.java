package org.mercurydb.queryutils;


/**
 * Simple class for JoinPredicates. This is used
 * for Joins like in the following statement:
 * 
 *  * For >1 predicates -- A.X=B.Y and B.C=C.D and A.Y=D.F
 * JoinDriver.join(
 *     new Predicate(TableA.joinX(), TableB.joinY()),
 *     new Predicate(TableB.joinC(), TableC.joinD()),
 *     new Predicate(TableA.joinY(), TableD.joinF()));
 */
public class JoinPredicate implements Comparable<JoinPredicate> {
	HgJoinInput stream1, stream2;
	public final HgRelation predicate;
	
	public JoinPredicate(HgJoinInput s1, HgJoinInput s2) {
		this(HgRelation.EQ, s1, s2);
	}
	
	public JoinPredicate(HgRelation predicate, HgJoinInput s1, HgJoinInput s2) {
		this.predicate = predicate;
		this.stream1 = s1;
		this.stream2 = s2;
	}
	
	private int numIndices() {
		int res = 0;
		if (stream1.isIndexed()) ++res;
		if (stream2.isIndexed()) ++res;
		return res;
	}
	
	private int cardinality() {
		return stream1.getCardinality() + stream2.getCardinality();
	}
	
	/**
	 * This method compares JoinPredicates with the 
	 * following ordering priority:
	 * 
	 * 1. Number of indices (greater first, i.e. descending)
	 * 2. Cardinality of its streams (smaller first, i.e. ascending [natural])
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
