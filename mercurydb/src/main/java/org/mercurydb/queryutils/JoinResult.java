package org.mercurydb.queryutils;

import java.util.HashSet;
import java.util.Set;

/**
 * JoinDriver join methods always return these.
 * 
 * @author colestewart
 *
 */
abstract public class JoinResult extends HgStream<JoinRecord> {
	private Set<Class<?>> containedTypes = new HashSet<>();
	private final JoinStream<?> a, b;
	
	public JoinResult(JoinStream<?> a, JoinStream<?> b) {
		super(a.cardinality + b.cardinality);
		this.a = a;
		this.b = b;
		this.containedTypes.addAll(a.containedTypes());
		this.containedTypes.addAll(b.containedTypes());
	}
	
	@Override 
	public Object extractField(FieldExtractable fe, Object instance) {
		JoinRecord jr = (JoinRecord)instance;
		return fe.extractField(jr.get(fe.getContainerClass()));
	}
	
	public Set<Class<?>> containedTypes() {
		return new HashSet<Class<?>>(containedTypes);
	}
	
	@Override
	public void reset() {
		a.reset();
		b.reset();
	}
}
