package org.mercurydb.queryutils;

public enum HgRelation {
    EQ,
    NE,
    LT,
    LE,
    GT,
    GE;

    public<T> boolean compare(T o1, T o2) {
        switch (this) {
            case EQ:
                return o1.equals(o2);
            case NE:
                return !o1.equals(o2);
        }

        if (o1 instanceof Comparable) {
            int result = ((Comparable) o1).compareTo(o2);
            switch (this) {
                case LT:
                    return result < 0;
                case LE:
                    return result <= 0;
                case GT:
                    return result > 0;
                case GE:
                    return result >= 0;
            }
        } else {
            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use" +
                    "LT, LE, GT, and GE relations.");
        }

        return false;
    }
}
