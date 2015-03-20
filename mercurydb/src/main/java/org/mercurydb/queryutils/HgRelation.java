package org.mercurydb.queryutils;

import java.util.Collection;

public class HgRelation {

    public static final HgBiPredicate<?, ?> EQ = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            return o1.equals(o2);
        }
    };

    public static final HgBiPredicate<?, ?> NE = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            return !o1.equals(o2);
        }
    };

    public static final HgBiPredicate<?, ?> LT = new HgBiPredicate<Object, Object>() {

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


    public static final HgBiPredicate<?, ?> GT = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) > 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    public static final HgBiPredicate<?, ?> GE = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) >= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    // TODO figure out if this should be templated as <?,? extends Collection<?>> instead
    public static final HgBiPredicate<?, ?> IN = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o2 instanceof Collection<?>) {
                return ((Collection<?>)o2).contains(o1);
            } else if (o1 instanceof Collection<?>) {
                return ((Collection<?>)o1).contains(o2);
            }

            throw new IllegalArgumentException("At least one argument must be a Collection!");
        }
    };
}
