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
public abstract class HgStream<T> implements Iterator<T>, Iterable<T> {
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

    public<F> HgStream<T> filter(final AbstractFieldExtractablePredicate<T,F>... preds) {
        return new HgStream<T>(this.cardinality) {
            private T next;
            private HgStream<T> stream = HgStream.this;

            @Override
            public boolean hasNext() {
                while (stream.hasNext()) {
                    next = stream.next();
                    boolean pass = true;

                    for (AbstractFieldExtractablePredicate<T,F> pred : preds) {
                        if (!pred.test((F) pred.extractField(next))) {
                            pass = false;
                            break;
                        }
                    }

                    if (pass) {
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

    public HgStream<T> concat(final HgStream<? extends T> stream) {
        return new HgStream<T>(cardinality + stream.cardinality) {
            HgStream<? extends T> a = HgStream.this;
            HgStream<? extends T> b = stream;

            @Override
            public void reset() {
                a.reset();
                b.reset();
            }

            @Override
            public boolean hasNext() {
                // I know this is a funny way of doing this, but keeping a as the current
                // stream and swapping out for b keeps us from checking both a's hasNext and b's hasNext
                // when a is out of elements.
                if (a.hasNext()) {
                    return true;
                }
                else if (b.hasNext()) {
                    HgStream<? extends T> tmpA = a;
                    a = b;
                    b = tmpA;
                    return true;
                }

                return false;
            }

            @Override
            public T next() {
                return a.next();
            }
        };
    }

    public Iterator<T> iterator() {
        reset();
        return HgStream.this;
    }
}
