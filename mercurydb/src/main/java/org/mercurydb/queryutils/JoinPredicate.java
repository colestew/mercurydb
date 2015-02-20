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
 *     
 * @author colestewart
 *
 */
public class JoinPredicate implements Comparable<JoinPredicate> {
	HgMonoStream<?> stream1, stream2;
	public final String relation;
	
	public JoinPredicate(HgMonoStream<?> s1, HgMonoStream<?> s2) {
		this("=", s1, s2);
	}
	
	public JoinPredicate(String relation, HgMonoStream<?> s1, HgMonoStream<?> s2) {
		this.relation = relation;
		this.stream1 = s1;
		this.stream2 = s2;
	}
	
	private int numIndices() {
		int res = 0;
		if (stream1.joinKey.isIndexed()) ++res;
		if (stream2.joinKey.isIndexed()) ++res;
		return res;
	}
	
	private int cardinality() {
		return stream1.cardinality() + stream2.cardinality();
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
