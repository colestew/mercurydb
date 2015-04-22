package org.mercurydb.queryutils;

public class ValueExtractableRelation<T, F> extends AbstractValueExtractablePredicate<T, F> {

    public final HgBiPredicate relation;

    public final F value;

    public ValueExtractableRelation(ValueExtractableSeed fe, HgBiPredicate<?, ?> rel, F value) {
        super(fe);
        this.relation = rel;
        this.value = value;
    }

    @Override
    public boolean test(F o) {
        return relation.test(o, value);
    }
}
