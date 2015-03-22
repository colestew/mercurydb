package org.mercurydb.queryutils;

import com.google.common.collect.Iterables;

import java.util.*;

public abstract class HgRelation implements HgBiPredicate<Object, Object> {
    abstract public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value);

    public static final HgRelation EQ = new HgRelation() {

        @Override
        public boolean test(Object o1, Object o2) {
            return o1.equals(o2);
        }

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            return noNull(index.get(value));
        }
    };

    public static final HgRelation NE = new HgRelation() {

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            Collection<Collection<Object>> iterables = new ArrayList<>(index.keySet().size()-1);

            for (Object key : index.keySet()) {
                if (!key.equals(value)) {
                    iterables.add(index.get(key));
                }
            }

            return Iterables.concat(iterables);
        }

        @Override
        public boolean test(Object o1, Object o2) {
            return !o1.equals(o2);
        }
    };

    public static final HgRelation LT = new HgRelation() {

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, Set<Object>> tIndex = (TreeMap)index;
                return Iterables.concat(tIndex.headMap(value, false).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) < 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };


    public static final HgRelation LE = new HgRelation() {

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, Set<Object>> tIndex = (TreeMap)index;
                return Iterables.concat(tIndex.headMap(value, true).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) <= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    public static final HgRelation GT = new HgRelation() {

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, Set<Object>> tIndex = (TreeMap)index;
                return Iterables.concat(tIndex.tailMap(value, false).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable)o1).compareTo(o2) > 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }
    };

    public static final HgRelation GE = new HgRelation() {

        @Override
        public Iterable<Object> getFromIndex(Map<Object, Set<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, Set<Object>> tIndex = (TreeMap)index;
                return Iterables.concat(tIndex.headMap(value, true).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

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

    private static Iterable<Object> noNull(Iterable<Object> result) {
        return result == null ? Collections.emptyList() : result;
    }
}
