package org.mercurydb.queryutils;

public interface Joinable {
    public HgTupleStream joinOn(FieldExtractable fe);
}
