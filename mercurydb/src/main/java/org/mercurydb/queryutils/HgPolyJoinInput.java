package org.mercurydb.queryutils;

import java.util.HashSet;
import java.util.Set;

/**
 * JoinDriver join methods always return these.
 */
abstract public class HgPolyJoinInput extends HgJoinInput {
	private final HgJoinInput a, b;
	
	public HgPolyJoinInput(HgJoinInput a, HgJoinInput b) {
		super(a, b);
		this.a = a;
		this.b = b;
	}
	
	@Override 
	public Object extractField(Object instance) {
		HgTuple jr = (HgTuple)instance;
		return _fwdFE.extractField(jr.get(_fwdFE.getContainerClass()));
	}
	
	@Override
	public void reset() {
		a.reset();
		b.reset();
	}
}
