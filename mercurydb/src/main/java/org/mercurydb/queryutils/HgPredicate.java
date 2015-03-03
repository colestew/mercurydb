package org.mercurydb.queryutils;

public interface HgPredicate<T> {
	public boolean test(T value);
}
