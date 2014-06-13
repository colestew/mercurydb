package javadb.queryutils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author colestewart
 *
 * @param <C>
 * @param <F>
 */
abstract public class Stream<C> implements Iterator<C> {
	protected int cardinality;
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void reset() {};
	
	public int cardinality() {
		return cardinality;
	}
	
	public Stream(int cardinality) {
		this.cardinality = cardinality;
	}
	
	public Object extractField(FieldExtractable fe, Object instance) {
		return fe.extractField(instance);
	}

	public JoinStream<C> joinOn(FieldExtractable f) {
		return new JoinStream<C>(this, f);
	}

	public Stream<C> filter(final FieldExtractable fe, Object... val) {
		final Set<Object> valSet = new HashSet<>(Arrays.asList(val));
		return new Stream<C>(Stream.this.cardinality) {
			private C next;
			private Stream<C> stream = Stream.this;

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
				Stream.this.reset();
				return Stream.this;
			}
		};
	}
}
