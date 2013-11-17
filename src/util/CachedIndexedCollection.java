package util;

import graph.core.UniqueID;

/**
 * A class for representing a collection of indexable objects. Objects are
 * 
 * @author Sam Sarjant
 */
public class CachedIndexedCollection<T extends UniqueID> extends
		MapIndexedCollection<T> {
	/**
	 * Constructor for a new IndexedCollection.
	 */
	public CachedIndexedCollection() {
		indexedMap_ = new CacheMap<>(true);
	}

	/**
	 * Constructor for a new IndexedCollection
	 * 
	 * @param cacheSize
	 *            The size of the cache.
	 */
	public CachedIndexedCollection(int cacheSize) {
		indexedMap_ = new CacheMap<>(cacheSize, true);
	}
}
