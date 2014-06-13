package javadb.queryutils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class IndexRetrieval<C> extends Stream<C> {
	private final Map<Object, Set<C>> index;
	
	@SuppressWarnings("unchecked")
	public IndexRetrieval(Map<?, Set<C>> index) {
		super(Sets.newHashSet(index.values()).size());
		
		this.index = (Map<Object, Set<C>>)index;
	}
	
	public Iterable<C> get(Object o) {
		return index.get(o);
	}
	
	public Iterable<Object> keys() {
		return new Iterable<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return index.keySet().iterator();
			}
			
		};
	}

	@Override
	public boolean hasNext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public C next() {
		throw new UnsupportedOperationException();
	}
}
