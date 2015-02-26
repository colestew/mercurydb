package org.mercurydb.queryutils;

import java.util.Iterator;

import com.google.common.collect.Iterables;

/**
 * Table classes will define these. Each 
 * instance has two possible implementations switched
 * by the isIndexed() method. If it is indexed, get(F i), 
 * hasNextKey(), and nextKey() are defined.
 * 
 * @param <C> The ClassType
 * @param <F> The FieldType
 */
public class Retrieval<C> extends HgStream<C> {
	private Iterable<C> streamSeed;
	private Iterator<C> stream;
	
	public Retrieval(Iterable<C> streamSeed, int cardinality) {
		super(cardinality);
		this.streamSeed = streamSeed;
		stream = streamSeed.iterator();
	}
	
	public Retrieval<C> join(Retrieval<? extends C> or) {
		streamSeed = Iterables.concat(streamSeed, or.streamSeed);
		super.cardinality += or.cardinality;
		return this;
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
