package org.mercurydb.queryutils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * // TODO documentation
 *
 * @param <T> // TODO documentation
 */
abstract public class HgStream<T> implements Iterator<T>, Iterable<T> {
    protected int cardinality;

    abstract public void reset();

    public int getCardinality() {
        return cardinality;
    }

    public HgStream(int cardinality) {
        this.cardinality = cardinality;
    }

    public HgTupleStream joinOn(FieldExtractable fe) {
        return HgTupleStream.createJoinInput(fe, this);
    }

    public HgStream<T> filter(final AbstractFieldExtractablePredicate<T> pred) {
        return new HgStream<T>(this.cardinality) {
            private T next;
            private HgStream<T> stream = HgStream.this;

            @Override
            public boolean hasNext() {
                while (stream.hasNext()) {
                    next = stream.next();

                    if (pred.test(next)) {
                        return true;
                    } else {
                        --cardinality;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                return next;
            }

            @Override
            public int getCardinality() {
                return cardinality;
            }

            @Override
            public void reset() {
                stream.reset();
            }
        };
    }

    public Iterator<T> iterator() {
        reset();
        return HgStream.this;
    }
}
