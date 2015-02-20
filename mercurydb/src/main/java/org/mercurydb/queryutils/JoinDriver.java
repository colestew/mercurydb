package org.mercurydb.queryutils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class is the main class used for join operations
 * on a javadb instance. It can be used like the following:
 * 
 * For 1 predicate -- A.X = B.Y
 * JoinDriver.join(TableA.joinX(), TableB.joinY());
 * 
 * For >1 predicates -- A.X=B.Y and B.C=C.D and A.Y=D.F
 * JoinDriver.join(
 *     new Predicate(TableA.joinX(), TableB.joinY()),
 *     new Predicate(TableB.joinC(), TableC.joinD()),
 *     new Predicate(TableA.joinY(), TableD.joinF()));
 * 
 * All join methods return a JoinResult, which is basically
 * an Iterator<JoinRecord>. Here is an example of a join method
 * in code and how to retrieve data values from JoinRecords:
 * 
 * for (JoinRecord jr : JoinDriver.join(TableA.joinX(), TableB.joinY())) {
 *     A x = (A)jr.get(A.class); // Always returns Object, so must cast
 *     B y = (B)jr.get(B.class); // Always returns Object, so must cast
 * }
 * 
 * @author colestewart
 */
public class JoinDriver {

	/**
	 * Joins a set of Predicates. Creates optimal
	 * join operation according to those rules defined
	 * in JoinPredicate.
	 * @param 1 or more join predicates
	 * @return a JoinResult of a join on preds
	 * @throws IllegalStateException if preds do not unify 
	 */
	public static HgPolyStream join(JoinPredicate... preds) {

		if (preds.length == 1) {
			return join(preds[0].relation, preds[0].stream1, preds[0].stream2);
		}
		else if (preds.length > 1) {
			Arrays.sort(preds);
			System.out.println("Joining " + preds[0].stream1.joinKey.getContainerClass() + 
					" x " + preds[0].stream2.joinKey.getContainerClass());
			HgPolyStream result = join(preds[0].stream1, preds[0].stream2);

			for (int i = 1; i < preds.length; ++i) {
				JoinPredicate p = preds[i];
				if (result.containedTypes().contains(p.stream1.joinKey.getContainerClass())) {
					p.stream1 = result.joinOn(p.stream1.joinKey);
				} else if (result.containedTypes().contains(p.stream2.joinKey.getContainerClass())) {
					p.stream2 = result.joinOn(p.stream2.joinKey);
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
	 * Returns a JoinResult using an equality predicate.
	 * @param a
	 * @param b
	 * @return
	 */
	public static HgPolyStream join(
			HgMonoStream<?> a,
			HgMonoStream<?> b) {
		return join("=", a, b);
	}


	/**
	 * Joins two JoinStreams and returns a JoinResult. A JoinResult
	 * is a JoinStream which is basically an iterator over JoinRecords.
	 * JoinRecords are essentially a Map of class types to Objects. So you
	 * can select Objects from the result based on their class type. It
	 * will select the correct join method based on the index status
	 * of its arguments.
	 * @param a JoinStream
	 * @param a JoinStream
	 * @return a JoinResult of a join on preds
	 */
	public static HgPolyStream join(
			String relation,
			HgMonoStream<?> a,
			HgMonoStream<?> b) {

		if (a.joinKey.getContainerClass().equals(b.joinKey.getContainerClass())) {
			/*
			 * Self Join
			 */
			throw new UnsupportedOperationException("Self joins not currently supported :(");
		} 
		else if (!a.containedTypes().retainAll(b.containedTypes()) 
				|| !b.containedTypes().retainAll(a.containedTypes())) {
			/*
			 * Filter operation
			 */
			return joinFilter(a, b);

		} else if (a.hasUsableIndex() && b.hasUsableIndex()) {
			/*
			 * Both A and B indexed
			 * Do index intersection A, B
			 */
			return joinIndexIntersection(a, b);

		} else if (a.hasUsableIndex() || b.hasUsableIndex()) {
			/* 
			 * Only A indexed
			 * Scan B, use A index
			 */
			return joinIndexScan(a, b);

		} else {
			/*
			 * Neither is indexed
			 * Do hash join
			 */
			return joinHash(a, b);
		}
	}

	/**
	 * Performs a filter join on two streams
	 * where one's set of contained types is 
	 * a subset of the other's.
	 * @param a
	 * @param b
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static HgPolyStream joinFilter(
			HgMonoStream<?> a, 
			HgMonoStream<?> b) {

		final HgMonoStream<HgTuple> ap;
		final HgMonoStream<Object> bp;

		// Perform Filter operation on A
		if (b.containedTypes().retainAll(a.containedTypes())) {
			ap = (HgMonoStream<HgTuple>)a; 
			bp = (HgMonoStream<Object>)b;
		} else {
			ap = (HgMonoStream<HgTuple>)b; 
			bp = (HgMonoStream<Object>)a;
		}

		return new HgPolyStream(a, b) {
			//Iterator<Object> aKeys = ap.keys().iterator();
			HgTuple currA;

			@Override
			public boolean hasNext() {
				if (ap.hasNext()) {
					currA = ap.next();
					Object jkv1o = ap.extractJoinKeyValue(currA);
					Object jkv2o = bp.extractJoinKeyValue(currA);
					if (jkv1o.equals(jkv2o)) return true;
					else return hasNext();
				}
				return false;
			}

			@Override
			public HgTuple next() {
				return currA;
			}
		};
	}

	/**
	 * Joins two JoinStreams. Both of which must be 
	 * indexed. Performs an intersection of two JoinStreams,
	 * both of which must be index retrievals.
	 * @param a
	 * @param b
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static HgPolyStream joinIndexIntersection(
			final HgMonoStream<?> a, 
			final HgMonoStream<?> b) {
		
		final IndexRetrieval<Object> ap = (IndexRetrieval<Object>)a.getWrappedStream();
		final IndexRetrieval<Object> bp = (IndexRetrieval<Object>)b.getWrappedStream();

		return new HgPolyStream(a, b) {
			Iterator<Object> aKeys = ap.keys().iterator();
			Iterator<Object> aInstances = Collections.emptyIterator();
			Object currA;
			Iterator<Object> bInstances = Collections.emptyIterator();
			Object currB;
			Iterable<Object> bSeed;

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
				while (aKeys.hasNext()) {
					Object currKey = aKeys.next();
					bSeed = bp.get(currKey);
					if (bSeed != null) {
						aInstances = ap.get(currKey).iterator();
						return hasNext();
					}
				}
				return false;
			}

			@Override
			public HgTuple next() {
				return new HgTuple(a, currA, b, currB);
			}
		};	
	}

	/**
	 * Joins two JoinStreams, one of which must
	 * be indexed. Scans over the non-indexed stream
	 * and uses the index on the indexed stream.
	 * @param a
	 * @param b
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static HgPolyStream joinIndexScan(
			final HgMonoStream<?> a,
			final HgMonoStream<?> b) {

		System.out.println("Performing Index Scan");
		final HgMonoStream<Object> ap;
		final HgMonoStream<Object> bp;

		if (a.hasUsableIndex()) {
			ap = (HgMonoStream<Object>)a;
			bp = (HgMonoStream<Object>)b;
		} else {
			ap = (HgMonoStream<Object>)b;
			bp = (HgMonoStream<Object>)a;
		}

		return new HgPolyStream(a, b) {
			Object currB;
			Iterator<Object> bInstances = bp;
			Iterator<Object> aInstances = Collections.emptyIterator();

			@Override
			public boolean hasNext() {
				if (aInstances.hasNext()) {
					return true;
				} else while (bInstances.hasNext()) {
					currB = bInstances.next();
					Object currKey = bp.extractJoinKeyValue(currB);
					Iterable<Object> aIterable = ((IndexRetrieval<Object>)ap.getWrappedStream()).get(currKey);
					if (aIterable != null) {
						aInstances = aIterable.iterator();
						return true;
					}
				}
				return false;
			}

			@Override
			public HgTuple next() {
				return new HgTuple(ap, aInstances.next(), bp, currB);
			}
		};
	}

	/**
	 * Simple hash join algorithm. Inhales the results
	 * into a 
	 * @param a
	 * @param b
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HgPolyStream joinHash(
			HgMonoStream<?> a,
			HgMonoStream<?> b) {
		System.out.println("Performing Hash Join.");
		final HgMonoStream<Object> ap;
		final HgMonoStream<Object> bp;

		if (a.cardinality() < b.cardinality()) {
			ap = (HgMonoStream<Object>)a;
			bp = (HgMonoStream<Object>)b;
		} else {
			ap = (HgMonoStream<Object>)b;
			bp = (HgMonoStream<Object>)a;
		}

		final Map<Object, Set<Object>> aMap = new HashMap<Object, Set<Object>>();

		// Inhale stream A into hash table
		for (Object aInstance : ap.elements()) {
			Object key = ap.extractJoinKeyValue(aInstance);

			Set<Object> l = aMap.get(key);
			if (l == null) {
				l = new HashSet<>();
			}
			l.add(aInstance);
			aMap.put(key, l);
		}

		IndexRetrieval<Object> indexStream = new IndexRetrieval<>(aMap);

		return joinIndexScan(indexStream.joinOn(ap.joinKey), bp);
	}

	/**
	 * Simple nested loops join algorithm.
	 * @param a
	 * @param b
	 * @return
	 */
	public static HgPolyStream joinNestedLoops(
			final HgMonoStream<?> a,
			final HgMonoStream<?> b) {

		return new HgPolyStream(a, b) {
			Object currA, currB;

			@Override
			public boolean hasNext() {
				while (b.hasNext() && currA != null) {
					currB = b.next();
					if (a.extractJoinKeyValue(currA)
							.equals(b.extractJoinKeyValue(currB))) {
						return true;
					}
				}
				if (a.hasNext()) {
					b.reset();
					currA = a.next();
					return hasNext();
				}

				return false;
			}

			@Override
			public HgTuple next() {
				return new HgTuple(a, currA, b, currB);
			}

		};
	}
}
