package util;

import graph.core.UniqueID;

import java.util.HashMap;

public class HashIndexedCollection<T extends UniqueID> extends MapIndexedCollection<T> {
	/**
	 * Constructor for a new IndexedCollection.
	 */
	public HashIndexedCollection() {
		indexedMap_ = new HashMap<>();
	}

	/**
	 * Constructor for a new IndexedCollection
	 * 
	 * @param cacheSize
	 *            The size of the cache.
	 */
	public HashIndexedCollection(int initialSize) {
		indexedMap_ = new HashMap<>(initialSize);
	}
}
