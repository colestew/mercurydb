package javadb.queryutils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JoinStream<C> extends Stream<C> {
	private Stream<C> stream;
	public final FieldExtractable joinKey;
	
	public JoinStream(Stream<C> stream, FieldExtractable joinKey) {
		super(stream.cardinality);
		this.joinKey = joinKey;
		this.stream = stream;
	}
	
	public Stream<C> getWrappedStream() {
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
		return new HashSet<Class<?>>(Collections.singleton(joinKey.getContainerClass()));
	}

	@Override
	public void reset() {
		stream.reset();
	}
}
