package org.mercurydb.queryutils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HgMonoStream<C> extends HgBaseStream<C> {
	private HgBaseStream<C> stream;
	public final FieldExtractable joinKey;
	
	public HgMonoStream(HgBaseStream<C> stream, FieldExtractable joinKey) {
		super(stream.cardinality);
		this.joinKey = joinKey;
		this.stream = stream;
	}
	
	public HgBaseStream<C> getWrappedStream() {
		return stream;
	}
	
	public Object extractJoinKeyValue(Object instance) {
		return stream.extractField(joinKey, instance);
	}
	
	public boolean hasUsableIndex() {
		return this.joinKey.isIndexed() && (stream instanceof IndexRetrieval);
	}
	
	@Override
	public boolean hasNext() {
		return stream.hasNext();
	}

	@Override
	public C next() {
		return stream.next();
	}
	
	public Set<Class<?>> containedTypes() {
		return Collections.singleton(joinKey.getContainerClass());
	}

	@Override
	public void reset() {
		stream.reset();
	}
}
