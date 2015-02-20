package org.mercurydb.queryutils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.BaseStream;

/**
 * 
 * @author colestewart
 *
 * @param <C>
 * @param <F>
 */
abstract public class HgBaseStream<C> implements Iterator<C> {
	protected int cardinality;
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void reset() {};
	
	public int cardinality() {
		return cardinality;
	}
	
	public HgBaseStream(int cardinality) {
		this.cardinality = cardinality;
	}
	
	public Object extractField(FieldExtractable fe, Object instance) {
		return fe.extractField(instance);
	}

	public HgMonoStream<C> joinOn(FieldExtractable f) {
		return new HgMonoStream<C>(this, f);
	}

	public HgBaseStream<C> filter(final FieldExtractable fe, Object... val) {
		final Set<Object> valSet = new HashSet<>(Arrays.asList(val));
		return new HgBaseStream<C>(HgBaseStream.this.cardinality) {
			private C next;
			private HgBaseStream<C> stream = HgBaseStream.this;

			@Override
			public boolean hasNext() {
				while (stream.hasNext()) {
					next = stream.next();
					if (!valSet.contains(stream.extractField(fe, next))) {
						--cardinality;
					} else {
						return true;
					}
				}
				return false;
			}

			@Override
			public C next() {
				return next;
			}
			
			@Override
			public int cardinality() {
				return cardinality;
			}
			
			@Override
			public void reset() {
				stream.reset();
			}
		};
	}

	public Iterable<C> elements() {
		return new Iterable<C>() {
			public Iterator<C> iterator() {
				HgBaseStream.this.reset();
				return HgBaseStream.this;
			}
		};
	}
}
