package org.mercurydb.queryutils;

import java.util.Set;

import com.google.common.collect.Sets;

public class HgMonoStream<C> extends HgStream<C> {
	private HgStream<C> stream;
	public final FieldExtractable joinKey;
	
	public HgMonoStream(HgStream<C> stream, FieldExtractable joinKey) {
		super(stream.cardinality);
		this.joinKey = joinKey;
		this.stream = stream;
	}
	
	public HgStream<C> getWrappedStream() {
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
		return Sets.newHashSet(joinKey.getContainerClass());
	}

	@Override
	public void reset() {
		stream.reset();
	}
}
