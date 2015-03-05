package org.mercurydb.queryutils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public abstract class HgJoinInput 
extends HgStream<HgTuple> implements FieldExtractable<Object, Object> {
	protected FieldExtractable<Object, Object> _fwdFE;
	protected Set<Class<?>> _containedTypes;
	
	public HgJoinInput(HgJoinInput a, HgJoinInput b) {
		super(0);
		a._containedTypes.addAll(b._containedTypes);
		this._containedTypes = a._containedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public HgJoinInput(FieldExtractable<Object, Object> fe) {
		this(fe, Sets.newHashSet(fe.getContainerClass()));
	}
	
	public HgJoinInput(
			FieldExtractable<Object,Object> fe,
			Set<Class<?>> containedTypes) {
		super(0); // TODO this needs some serious analysis
		this._fwdFE = fe;
		this._containedTypes = containedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public void setJoinKey(FieldExtractable<?,?> fe) {
		this._fwdFE = (FieldExtractable<Object, Object>) fe;
	}
	
	public Set<Class<?>> getContainedTypes() {
		return _containedTypes;
	}
	
	@Override
	public Class<?> getContainerClass() {
		return _fwdFE.getContainerClass();
	}

	@Override
	public Object extractField(Object o) {
		return _fwdFE.extractField(o);
	}

	@Override
	public boolean isIndexed() {
		return _fwdFE.isIndexed();
	}

	@Override
	public Map<Object, Set<Object>> getIndex() {
		return _fwdFE.getIndex();
	}

	@Override
	public int getContainerId() {
		return _fwdFE.getContainerId();
	}
	
	public Iterator<Object> getObjectIterator() {
		return new Iterator<Object>() {

			@Override
			public boolean hasNext() {
				return HgJoinInput.this.hasNext();
			}

			@Override
			public Object next() {
				return HgJoinInput.this.next().get(_fwdFE.getContainerClass());
			}
		};
	}
	
	public static<F> HgJoinInput createJoinInput(
			FieldExtractable<?,F> fe,
			HgStream<?> stream) {
		FieldExtractable<Object, Object> feo = (FieldExtractable<Object, Object>) fe;
		return new HgJoinInput(feo) {

			@Override
			public boolean hasNext() {
				return stream.hasNext();
			}

			@Override
			public HgTuple next() {
				return HgTuple.singleton(this, stream.next());
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
}
