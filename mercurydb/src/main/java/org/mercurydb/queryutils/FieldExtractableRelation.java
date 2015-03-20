package org.mercurydb.queryutils;

public class FieldExtractableRelation<T, F> extends AbstractFieldExtractablePredicate<T, F> {

    public final HgBiPredicate relation;

    public final F value;

    public FieldExtractableRelation(FieldExtractableSeed fe, HgBiPredicate<?,?> rel, F value) {
        super(fe);
        this.relation = rel;
        this.value = value;
    }

    @Override
    public boolean test(F o) {
        return relation.test(o, value);
    }
}
