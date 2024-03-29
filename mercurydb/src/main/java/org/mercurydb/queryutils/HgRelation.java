package org.mercurydb.queryutils;

import com.google.common.collect.Iterables;

import java.util.*;

public abstract class HgRelation implements HgBiPredicate<Object, Object> {
    abstract public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value);


    abstract public HgRelation reversedRelation();

    public static final HgRelation EQ = new HgRelation() {
        @Override
        public boolean test(Object o1, Object o2) {
            return o1.equals(o2);
        }

        @Override
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            return noNull(index.get(value));
        }

        @Override
        public HgRelation reversedRelation() {
            return this;
        }
    };

    public static final HgRelation NE = new HgRelation() {
        @Override
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            Set<Object> keySet = index.keySet();
            Collection<Collection<Object>> iterables = new ArrayList<>(keySet.size() - 1);

            // TODO convert this to stream and collect but the given refactoring gives errors
            for (Object key : keySet) {
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

        @Override
        public HgRelation reversedRelation() {
            return this;
        }
    };

    public static final HgRelation LT = new HgRelation() {
        @Override
        @SuppressWarnings("unchecked") // cast to TreeMap
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, ? extends Collection<Object>> tIndex = (TreeMap) index;
                return Iterables.concat(tIndex.headMap(value, false).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        @SuppressWarnings("unchecked") // cast to Comparable
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2) < 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }

        @Override
        public HgRelation reversedRelation() {
            return GT;
        }
    };


    public static final HgRelation LE = new HgRelation() {
        @Override
        @SuppressWarnings("unchecked") // cast to TreeMap
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, ? extends Collection<Object>> tIndex = (TreeMap) index;
                return Iterables.concat(tIndex.headMap(value, true).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        @SuppressWarnings("unchecked") // cast to Comparable
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2) <= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }

        @Override
        public HgRelation reversedRelation() {
            return GE;
        }
    };

    public static final HgRelation GT = new HgRelation() {
        @Override
        @SuppressWarnings("unchecked") // cast to TreeMap
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, ? extends Collection<Object>> tIndex = (TreeMap) index;
                return Iterables.concat(tIndex.tailMap(value, false).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        @SuppressWarnings("unchecked") // cast to Comparable
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2) > 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }

        @Override
        public HgRelation reversedRelation() {
            return LT;
        }
    };

    public static final HgRelation GE = new HgRelation() {
        @Override
        @SuppressWarnings("unchecked") // cast to TreeMap
        public Iterable<Object> getFromIndex(Map<Object, ? extends Collection<Object>> index, Object value) {
            if (index instanceof TreeMap) {
                TreeMap<Object, ? extends Collection<Object>> tIndex = (TreeMap) index;
                return Iterables.concat(tIndex.tailMap(value, true).values());
            }

            throw new IllegalArgumentException("index must be ordered to use inequality relations!");
        }

        @Override
        @SuppressWarnings("unchecked") // cast to Comparable
        public boolean test(Object o1, Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2) >= 0;
            }

            throw new IllegalArgumentException("Arguments must implement the Comparable interface to use inequality relations");
        }

        @Override
        public HgRelation reversedRelation() {
            return LE;
        }
    };

    // TODO figure out if this should be templated as <?,? extends Collection<?>> instead
    public static final HgBiPredicate<?, ?> IN = new HgBiPredicate<Object, Object>() {

        @Override
        public boolean test(Object o1, Object o2) {
            if (o2 instanceof Collection<?>) {
                return ((Collection<?>) o2).contains(o1);
            } else if (o1 instanceof Collection<?>) {
                return ((Collection<?>) o1).contains(o2);
            }

            throw new IllegalArgumentException("At least one argument must be a Collection!");
        }
    };

    private static Iterable<Object> noNull(Iterable<Object> result) {
        return result == null ? Collections.emptyList() : result;
    }
}
