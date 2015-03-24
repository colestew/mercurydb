package org.mercurydb.queryutils;

public interface HgBiPredicate<T1, T2> {
    public boolean test(T1 o1, T2 o2);
}

