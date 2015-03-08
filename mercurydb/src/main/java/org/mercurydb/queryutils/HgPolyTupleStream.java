package org.mercurydb.queryutils;

/**
 * JoinDriver join methods always return these.
 */
abstract public class HgPolyTupleStream extends HgTupleStream {
    private final HgTupleStream a, b;

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
