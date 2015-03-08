package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

public class FieldExtractableRelation<T> extends AbstractFieldExtractablePredicate<T> {

    public final HgRelation relation;

    public final Object value;

    public FieldExtractableRelation(FieldExtractableSeed fe, HgRelation rel, Object value) {
        super(fe);
        this.relation = rel;
        this.value = value;
    }

    @Override
    public boolean test(Object o) {
        return relation.compare(o, value);
    }
}
