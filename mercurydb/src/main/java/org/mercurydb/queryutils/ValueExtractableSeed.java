package org.mercurydb.queryutils;

/**
 * This is the beginning. This class exists so that the query methods
 * have access to the streams from the tables. There is one additional method
 * to get access to the stream from the table. For one table class this stream should
 * always be the same.
 */
public abstract class ValueExtractableSeed<T> implements ValueExtractable {
    private final TableID<T> _id;

    public ValueExtractableSeed(TableID<T> id) {
        this._id = id;
    }

    abstract public HgStream<T> getDefaultStream();

    @Override
    public TableID<T> getTableId() {
        return _id;
    }
}
