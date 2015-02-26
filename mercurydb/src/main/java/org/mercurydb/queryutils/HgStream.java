package org.mercurydb.queryutils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.BaseStream;

abstract public class HgStream<T> implements Iterator<T> {
	protected int cardinality;
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void reset() {}
	
	public int cardinality() {
		return cardinality;
	}
	
	public HgStream(int cardinality) {
		this.cardinality = cardinality;
	}
	
	public Object extractField(FieldExtractable fe, Object instance) {
		return fe.extractField(instance);
	}

	public HgMonoStream<T> joinOn(FieldExtractable f) {
		return new HgMonoStream<T>(this, f);
	}

	public HgStream<T> filter(final FieldExtractable fe, Object... val) {
		final Set<Object> valSet = new HashSet<>(Arrays.asList(val));
		return new HgStream<T>(this.cardinality) {
			private T next;
			private HgStream<T> stream = HgStream.this;

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
			public T next() {
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

	public Iterable<T> elements() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				HgStream.this.reset();
				return HgStream.this;
			}
		};
	}
}
