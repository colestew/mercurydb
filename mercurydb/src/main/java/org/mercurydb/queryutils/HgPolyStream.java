package org.mercurydb.queryutils;

import java.util.HashSet;
import java.util.Set;

/**
 * JoinDriver join methods always return these.
 */
abstract public class HgPolyStream extends HgStream<HgTuple> {
	private Set<Class<?>> containedTypes = new HashSet<>();
	private final HgMonoStream<?> a, b;
	
	public HgPolyStream(HgMonoStream<?> a, HgMonoStream<?> b) {
		super(a.cardinality + b.cardinality);
		this.a = a;
		this.b = b;
		this.containedTypes.addAll(a.containedTypes());
		this.containedTypes.addAll(b.containedTypes());
	}
	
	@Override 
	public Object extractField(FieldExtractable fe, Object instance) {
		HgTuple jr = (HgTuple)instance;
		return fe.extractField(jr.get(fe.getContainerClass()));
	}
	
	public Set<Class<?>> containedTypes() {
		return containedTypes;
	}
	
	@Override
	public void reset() {
		a.reset();
		b.reset();
	}
}
