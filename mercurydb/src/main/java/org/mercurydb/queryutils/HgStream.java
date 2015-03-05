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

    public <F> HgJoinInput joinOn(FieldExtractable<?, F> fe) {
        return HgJoinInput.createJoinInput(fe, this);
    }

    // TODO understand heap pollution via template varargs
    @SuppressWarnings("unchecked")
    public <F> HgStream<T> filter(final FieldExtractable<T, F> fe, F... val) {
        final Set<Object> valSet = new HashSet<Object>(Arrays.asList(val));
        return new HgStream<T>(this.cardinality) {
            private T next;
            private HgStream<T> stream = HgStream.this;

            @Override
            public boolean hasNext() {
                while (stream.hasNext()) {
                    next = stream.next();
                    if (!valSet.contains(fe.extractField(next))) {
                        --cardinality;
                    } else {
                        return true;
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
        HgStream.this.reset();
        return HgStream.this;
    }
}
