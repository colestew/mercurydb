package org.mercurydb.queryutils;

/**
 * JoinDriver join methods always return these.
 */
public abstract class HgPolyTupleStream extends HgTupleStream {
    public final HgTupleStream a, b;

    public HgPolyTupleStream(HgTupleStream a, HgTupleStream b) {
        super(a, b);
        this.a = a;
        this.b = b;
    }

    @Override
    public void reset() {
        a.reset();
        b.reset();
    }
}
