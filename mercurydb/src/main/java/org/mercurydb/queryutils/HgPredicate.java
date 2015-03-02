package org.mercurydb.queryutils;

public interface HgPredicate<T> {
	public boolean predicate(T value);
}
