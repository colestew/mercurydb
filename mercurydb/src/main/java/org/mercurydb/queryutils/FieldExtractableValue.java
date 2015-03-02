package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @param <T> Type that the table contains. (e.g. CustomerTable -> Customer)
 * @param <F> Field type.
 */
public class FieldExtractableValue<T, F> implements FieldExtractable<T, F> {

	private final FieldExtractable<T,F> _fwd;
	
	public final F value;
	
	public FieldExtractableValue(FieldExtractable<T,F> src, F value) {
		this._fwd = src;
		this.value = value;
	}
	
	@Override
	public Class<T> getContainerClass() {
		return _fwd.getContainerClass();
	}

	@Override
	public F extractField(T o) {
		return _fwd.extractField(o);
	}

	@Override
	public boolean isIndexed() {
		return _fwd.isIndexed();
	}

	@Override
	public Map<F,Set<T>> getIndex() {
		return _fwd.getIndex();
	}

	@Override
	public int getContainerId() {
		return _fwd.getContainerId();
	}
}
