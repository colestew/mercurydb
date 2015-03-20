package org.mercurydb.queryutils;

public class HgRelation {

    public static final HgBiPredicate<Object, Object> EQ = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            return o1.equals(o2);
        }
    };

    public static final HgBiPredicate<Object, Object> NE = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            return !o1.equals(o2);
        }
    };

    public static final HgBiPredicate<Object, Object> LT = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) < 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    public static final HgBiPredicate<Object, Object> LE = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) <= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };


    public static final HgBiPredicate<Object, Object> GT = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) > 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    public static final HgBiPredicate<Object, Object> GE = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) >= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };
}
