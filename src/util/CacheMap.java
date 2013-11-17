package util;

import java.util.LinkedHashMap;

/**
 * A map that has a maximum size for caching values.
 * 
 * @author Sam Sarjant
 */
public class CacheMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1676602354287340135L;

	private static final int DEFAULT_CACHE_SIZE = 512;

	/**
	 * The maximum size of this map.
	 */
	private int cacheSize_ = DEFAULT_CACHE_SIZE;

	private boolean orderByAccess_;

	/**
	 * Constructor for a new CacheMap
	 */
	public CacheMap(boolean orderByAccess) {
		super((int) (DEFAULT_CACHE_SIZE / 0.75));
		orderByAccess_ = orderByAccess;
	}

	/**
	 * Constructor for a new CacheMap
	 * 
	 * @param cacheSize
	 *            The size of the cache
	 * @param orderByAccess
	 *            If elements move to the front when accessed.
	 */
	public CacheMap(int cacheSize, boolean orderByAccess) {
		super((int) (cacheSize / 0.75));
		cacheSize_ = cacheSize;
		orderByAccess_ = orderByAccess;
	}

	/**
	 * Constructor for a new CacheMap
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public CacheMap(int cacheSize, float loadFactor) {
		super((int) (cacheSize / loadFactor));
		cacheSize_ = cacheSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V val = super.get(key);
		if (orderByAccess_ && val != null)
			put((K) key, val);
		return val;
	}

	@Override
	public synchronized V put(K key, V value) {
		V oldVal = remove(key);
		super.put(key, value);
		return oldVal;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > cacheSize_;
	}
}
