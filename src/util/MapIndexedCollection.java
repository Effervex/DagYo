package util;

import graph.core.UniqueID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class MapIndexedCollection<T extends UniqueID> implements
		IndexedCollection<T> {
	protected Map<Long, T> indexedMap_;

	/**
	 * Constructor for a new IndexedCollection.
	 */
	public MapIndexedCollection() {
	}

	/**
	 * Constructor for a new IndexedCollection
	 * 
	 * @param cacheSize
	 *            The size of the cache.
	 */
	public MapIndexedCollection(int initialSize) {
	}

	@Override
	public boolean add(T e) {
		if (e != null)
			return indexedMap_.put(e.getID(), e) == null;
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T e : c)
			changed |= add(e);
		return changed;
	}

	@Override
	public void clear() {
		indexedMap_.clear();
	}

	@Override
	public boolean contains(Object o) {
		if (o == null || !(o instanceof UniqueID))
			return false;

		T e = indexedMap_.get(((UniqueID) o).getID());
		return o.equals(e);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean contains = true;
		for (Object o : c)
			contains &= contains(o);
		return contains;
	}

	/**
	 * Get an item by ID.
	 * 
	 * @param id
	 *            The ID index to get an item by.
	 * @return The item if it exists, or null.
	 */
	@Override
	public T get(long id) {
		return indexedMap_.get(id);
	}

	@Override
	public boolean isEmpty() {
		return indexedMap_.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return indexedMap_.values().iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (o != null && o instanceof UniqueID)
			return indexedMap_.remove(((UniqueID) o).getID()) != null;
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed |= remove(o);
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Collection<T> removables = new ArrayList<>();
		for (Long id : indexedMap_.keySet()) {
			if (!c.contains(indexedMap_.get(id)))
				removables.add(indexedMap_.get(id));
		}
		return removeAll(removables);
	}

	@Override
	public int size() {
		return indexedMap_.size();
	}

	@Override
	public Object[] toArray() {
		return indexedMap_.values().toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return indexedMap_.values().toArray(a);
	}

	@Override
	public void setSize(int size) {
		// Do nothing
	}

	@Override
	public void update(T element) {
		// Do nothing
	}
}
