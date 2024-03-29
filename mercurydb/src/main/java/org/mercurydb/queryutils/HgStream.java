package org.mercurydb.queryutils;

import java.util.Iterator;

/**
 * // TODO documentation
 *
 * @param <T> // TODO documentation
 */
public abstract class HgStream<T> implements Iterator<T>, Iterable<T>, Joinable {
    abstract public void reset();

    @SafeVarargs
    public final <F> HgStream<T> filter(final AbstractValueExtractablePredicate<T, F>... preds) {
        return new HgStream<T>() {

            private T next;
            private HgStream<T> stream = HgStream.this;

            @Override
            @SuppressWarnings("unchecked") // for cast to (F)
            public boolean hasNext() {
                while (stream.hasNext()) {
                    next = stream.next();
                    boolean pass = true;

                    for (AbstractValueExtractablePredicate<T, F> pred : preds) {
                        if (!pred.test((F) pred.extractValue(next))) {
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
            public void reset() {
                stream.reset();
            }

            @Override
            public HgTupleStream joinOn(ValueExtractable fe) {
                return HgTupleStream.createJoinInput(fe, this, true);
            }
        };
    }

    public HgStream<T> concat(final HgStream<? extends T> stream) {
        return new HgStream<T>() {
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
                } else if (b.hasNext()) {
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

            @Override
            public HgTupleStream joinOn(ValueExtractable fe) {
                return HgTupleStream.createJoinInput(fe, this, true);
            }
        };
    }

    public Iterator<T> iterator() {
        reset();
        return HgStream.this;
    }
}
