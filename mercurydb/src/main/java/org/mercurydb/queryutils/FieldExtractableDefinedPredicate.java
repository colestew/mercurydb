package org.mercurydb.queryutils;

public class FieldExtractableDefinedPredicate<T, F>
        extends FieldExtractableValue<T, F> {

    private HgRelation rel;

    public FieldExtractableDefinedPredicate(FieldExtractable<T, F> fe, F val, HgRelation rel) {
        super(fe, val);

        this.rel = rel;
    }
}
