package javadb.queryutils;

import java.util.Iterator;
import java.util.Map;

/**
 * Table classes will define these. Each 
 * instance has two possible implementations switched
 * by the isIndexed() method. If it is indexed, get(F i), 
 * hasNextKey(), and nextKey() are defined.
 * 
 * @param <C> The ClassType
 * @param <F> The FieldType
 * 
 * @author colestew
 */
public class Retrieval<C> extends Stream<C> {
	private final Iterable<C> streamSeed;
	private Iterator<C> stream;
	
	public Retrieval(Iterable<C> streamSeed, int cardinality) {
		super(cardinality);
		this.streamSeed = streamSeed;
		stream = streamSeed.iterator();
	}

	@Override
	public boolean hasNext() {
		return stream.hasNext();
	}

	@Override
	public C next() {
		return stream.next();
	}

	@Override
	public void reset() {
		stream = streamSeed.iterator();
	}
}
