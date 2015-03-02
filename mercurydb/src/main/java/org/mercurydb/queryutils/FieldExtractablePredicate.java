package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

public class FieldExtractablePredicate<T,F> implements FieldExtractable<T,F> {
	
	private final FieldExtractable<T,F> _fwd;
	
	public final HgPredicate<F> predicate;
	
	public FieldExtractablePredicate(FieldExtractable<T,F> fe, HgPredicate<F> pred) {
		this._fwd = fe;
		this.predicate = pred;
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
	
	public boolean test(F value) {
		return predicate.predicate(value);
	}
}
