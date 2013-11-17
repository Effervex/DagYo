/*
 *    This file is part of the CERRLA algorithm
 *
 *    CERRLA is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    CERRLA is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with CERRLA. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    src/util/MultiMap.java
 *    Copyright (C) 2012 Samuel Sarjant
 */
package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class MultiMap<K, V> implements Serializable {
	private static final long serialVersionUID = -7151854312371461385L;
	private static final int LIST_COLLECTION = 0;
	private static final int SORTED_SET_COLLECTION = 1;
	private static final int CONCURRENT_HASH_COLLECTION = 2;
	/** An inner map containing the values. C is implicitly of type V. */
	private Map<K, Collection<V>> innerMap_;
	/** A flag which notes which type of collection this multimap implements. */
	private int collectionType_ = -1;
	/** An optional comparator used for the sorted set. */
	private Comparator<V> comparator_;

	/**
	 * The constructor.
	 */
	private MultiMap() {
		innerMap_ = new HashMap<K, Collection<V>>();
	}

	/**
	 * A factory method for creating a list type multimap.
	 * 
	 * @return A multimap implementing lists.
	 */
	public static <K, V> MultiMap<K, V> createListMultiMap() {
		MultiMap<K, V> listMultiMap = new MultiMap<K, V>();
		listMultiMap.collectionType_ = LIST_COLLECTION;
		return listMultiMap;
	}

	/**
	 * A factory method for creating a sorted set type multimap.
	 * 
	 * @return A multimap implementing sorted sets.
	 */
	public static <K, V> MultiMap<K, V> createSortedSetMultiMap() {
		MultiMap<K, V> ssMultiMap = new MultiMap<K, V>();
		ssMultiMap.collectionType_ = SORTED_SET_COLLECTION;
		return ssMultiMap;
	}

	public static <K, V> MultiMap<K, V> createConcurrentHashSetMultiMap() {
		MultiMap<K, V> chMultiMap = new MultiMap<K, V>();
		chMultiMap.collectionType_ = CONCURRENT_HASH_COLLECTION;
		return chMultiMap;
	}

	/**
	 * A factory method for creating a sorted set type multimap.
	 * 
	 * @param valueComparator
	 *            The comparator to use for the sorted set.
	 * @return A multimap implementing sorted sets.
	 */
	public static <K, V> MultiMap<K, V> createSortedSetMultiMap(
			Comparator<V> valueComparator) {
		MultiMap<K, V> ssMultiMap = new MultiMap<K, V>();
		ssMultiMap.collectionType_ = SORTED_SET_COLLECTION;
		ssMultiMap.comparator_ = valueComparator;
		return ssMultiMap;
	}

	public MultiMap(MultiMap<K, V> mm) {
		this();
		collectionType_ = mm.collectionType_;
		putAll(mm);
	}

	/**
	 * Initialises/gets the list under a key.
	 * 
	 * @param key
	 *            The key to get the list from.
	 * @return The newly created/pre-existing list.
	 */
	private Collection<V> initialiseGetCollection(K key) {
		// Initialise the list
		Collection<V> collection = innerMap_.get(key);
		if (collection == null) {
			if (collectionType_ == LIST_COLLECTION)
				collection = new ArrayList<V>();
			else if (collectionType_ == SORTED_SET_COLLECTION) {
				if (comparator_ != null)
					collection = new TreeSet<V>(comparator_);
				else
					collection = new TreeSet<V>();
			} else if (collectionType_ == CONCURRENT_HASH_COLLECTION)
				collection = Collections
						.newSetFromMap(new ConcurrentHashMap<V, Boolean>());
			innerMap_.put(key, collection);
		}
		return collection;
	}

	/**
	 * Clears the entire multimap.
	 */
	public void clear() {
		innerMap_.clear();
	}

	/**
	 * Clears the values from a key.
	 * 
	 * @param key
	 *            The key for clearing values under the key.
	 * @return True if the key is valid, false otherwise.
	 */
	public boolean clearValues(K key) {
		if (key == null)
			return false;
		innerMap_.put(key, null);
		return true;
	}

	/**
	 * Checks if the multimap contains the specified key.
	 * 
	 * @param key
	 *            The key being searched.
	 * @return True if the key is present.
	 */
	public boolean containsKey(Object key) {
		return innerMap_.containsKey(key);
	}

	/**
	 * Checks if the multimap contains the specified value (not collection).
	 * 
	 * @param The
	 *            value being searched for.
	 * @return True if the value is present within any of the collection values
	 *         in the multimap.
	 */
	public boolean containsValue(Object value) {
		for (K key : keySet()) {
			if (innerMap_.get(key).contains(value))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the multimap contains a specified list as a mapped value.
	 * 
	 * @param list
	 *            The list being searched for.
	 * @return True if the list is a mapped value.
	 */
	@SuppressWarnings("rawtypes")
	public boolean containsList(List list) {
		return innerMap_.containsValue(list);
	}

	/**
	 * Gets the collection of values under the multimap key.
	 * 
	 * @param key
	 *            The key to retrieve the values from.
	 * @return The collection under the key, or null.
	 */
	public Collection<V> get(Object key) {
		return innerMap_.get(key);
	}

	/**
	 * Gets the list of values under the multimap key.
	 * 
	 * @param key
	 *            The key to retrieve values from.
	 * @return The list under the key, or null.
	 */
	public List<V> getList(Object key) throws IllegalAccessException {
		if (collectionType_ == LIST_COLLECTION)
			return (List<V>) innerMap_.get(key);
		throw new IllegalAccessError(
				"MultiMap is not of collection type List. collectionType_: "
						+ collectionType_);
	}

	/**
	 * Gets the sorted set of values under the multimap key.
	 * 
	 * @param key
	 *            The key to retrieve values from.
	 * @return The sorted set under the key, or null.
	 */
	public SortedSet<V> getSortedSet(Object key) throws IllegalAccessException {
		if (collectionType_ == SORTED_SET_COLLECTION)
			return (SortedSet<V>) innerMap_.get(key);
		throw new IllegalAccessError(
				"MultiMap is not of collection type SortedSet. collectionType_: "
						+ collectionType_);
	}

	/**
	 * Gets the sorted set of values under the multimap key.
	 * 
	 * @param key
	 *            The key to retrieve values from.
	 * @return The sorted set under the key, or null.
	 */
	public Set<V> getConcurrentHashSet(Object key)
			throws IllegalAccessException {
		if (collectionType_ == CONCURRENT_HASH_COLLECTION)
			return (Set<V>) innerMap_.get(key);
		throw new IllegalAccessError(
				"MultiMap is not of collection type ConcurrentHashSet. collectionType_: "
						+ collectionType_);
	}

	/**
	 * Gets the element at a specified index in the multimap value.
	 * 
	 * @param key
	 *            The key for the value.
	 * @param index
	 *            The index of the element at the key.
	 * @return The value at the index, or null if the value doesn't exist or the
	 *         index is out of range.
	 */
	public V getIndex(Object key, int index) throws IllegalAccessException {
		if (collectionType_ == LIST_COLLECTION) {
			List<V> list = getList(key);
			if ((list != null) && (index < list.size()))
				return list.get(index);
			throw new IndexOutOfBoundsException();
		}
		throw new IllegalAccessError(
				"MultiMap is not of collection type List. collectionType_: "
						+ collectionType_);
	}

	/**
	 * Checks if the entire multimap is empty.
	 * 
	 * @return True if the multimap is empty.
	 */
	public boolean isKeysEmpty() {
		return innerMap_.isEmpty();
	}

	/**
	 * Returns true if the collection under this key is empty.
	 * 
	 * @param key
	 *            The key for the collection.
	 * @return True if the collection is empty.
	 */
	public boolean isValueEmpty(Object key) {
		Collection<V> values = innerMap_.get(key);
		if (values == null)
			return true;
		return values.isEmpty();
	}

	/**
	 * Checks if all values under every key are empty.
	 * 
	 * @return True if all values are empty.
	 */
	public boolean allValuesEmpty() {
		for (K key : keySet()) {
			if (!isValueEmpty(key))
				return false;
		}
		return true;
	}

	/**
	 * Gets the set of keys for this multimap.
	 * 
	 * @return The set of keys.
	 */
	public Set<K> keySet() {
		return innerMap_.keySet();
	}

	/**
	 * Adds a singular value to the multimap collection under a key.
	 * 
	 * @param key
	 *            The key to add the value to.
	 * @param value
	 *            The value to add to the collection.
	 * @return The resultant collection, containing the value.
	 */
	public Collection<V> put(K key, V value) {
		Collection<V> resultantCollection = initialiseGetCollection(key);

		// Adding the values
		if (value != null)
			resultantCollection.add(value);

		return resultantCollection;
	}

	/**
	 * Puts all values in a collection into the multimap.
	 * 
	 * @param key
	 *            The key to put the values under.
	 * @param collection
	 *            The collection containing the values to add.
	 * @return The resultant collection.
	 */
	public Collection<V> putCollection(K key, Collection<? extends V> collection) {
		Collection<V> resultantCollection = initialiseGetCollection(key);

		// Adding the values
		resultantCollection.addAll(collection);

		return resultantCollection;
	}

	/**
	 * Puts all values from a multi-map into this multi-map, but not
	 * overwriting.
	 * 
	 * @param mm
	 *            The multimap to add.
	 */
	public void putAll(MultiMap<? extends K, ? extends V> mm) {
		Set<? extends K> keySet = mm.keySet();
		for (K key : keySet) {
			putCollection(key, mm.get(key));
		}
	}

	/**
	 * Puts all values from a map into this multi-map, but not overwriting.
	 * 
	 * @param map
	 *            The map to add.
	 */
	public void putAll(Map<? extends K, ? extends V> map) {
		for (K key : map.keySet())
			put(key, map.get(key));
	}

	/**
	 * Puts all values from a multi-map into this multi-map, but not
	 * overwriting.
	 * 
	 * @param mm
	 *            The multimap to add.
	 * @return True if the map was changed at all, false otherwise.
	 */
	public boolean putAllContains(MultiMap<? extends K, ? extends V> mm) {
		Set<? extends K> keySet = mm.keySet();
		boolean result = false;
		for (K key : keySet) {
			result |= putContains(key, mm.get(key));
		}

		return result;
	}

	/**
	 * Adds a singular value to the multimap collection under a key if it is not
	 * already there.
	 * 
	 * @param key
	 *            The key to add the value to.
	 * @param value
	 *            The value to add to the collection, unless it is already
	 *            there.
	 * @return True if the value was added.
	 */
	public boolean putContains(K key, V value) {
		Collection<V> resultantCollection = initialiseGetCollection(key);

		// Adding the values
		if (collectionType_ == LIST_COLLECTION) {
			if (!resultantCollection.contains(value)) {
				resultantCollection.add(value);
				return true;
			}
		} else if (collectionType_ == SORTED_SET_COLLECTION
				|| collectionType_ == CONCURRENT_HASH_COLLECTION)
			return resultantCollection.add(value);

		return false;
	}

	/**
	 * Puts all values in a collection into the multimap.
	 * 
	 * @param key
	 *            The key to put the values under.
	 * @param collection
	 *            The collection containing the values to add.
	 * @return True if the value was added.
	 */
	public boolean putContains(K key, Collection<? extends V> collection) {
		Collection<V> resultantCollection = initialiseGetCollection(key);

		// Adding the values
		boolean result = false;
		if (collectionType_ == LIST_COLLECTION) {
			for (V value : collection) {
				if (!resultantCollection.contains(value)) {
					resultantCollection.add(value);
					result = true;
				}
			}
		} else if (collectionType_ == SORTED_SET_COLLECTION
				|| collectionType_ == CONCURRENT_HASH_COLLECTION) {
			return resultantCollection.addAll(collection);
		}

		return result;
	}

	/**
	 * Explicitly replaces a value if it is equal to the value being added.
	 * 
	 * @param key
	 *            The key to place the value under.
	 * @param value
	 *            The object that is guaranteed to be added to the collection.
	 */
	public void putReplace(K key, V value) {
		Collection<V> resultantCollection = initialiseGetCollection(key);

		// If list, replace
		if (collectionType_ == LIST_COLLECTION) {
			List<V> listCollection = (List<V>) resultantCollection;
			if (resultantCollection.contains(value))
				listCollection.set(listCollection.indexOf(value), value);
			else
				resultantCollection.add(value);
		} else if (collectionType_ == SORTED_SET_COLLECTION
				|| collectionType_ == CONCURRENT_HASH_COLLECTION) {
			// If set, it'll replace anyway
			resultantCollection.add(value);
		}
	}

	/**
	 * Removes a key from the multimap and returns the list contained under the
	 * key.
	 * 
	 * @param key
	 *            The key to remove.
	 * @return The list contained under the key.
	 */
	public Collection<V> remove(Object key) {
		return innerMap_.remove(key);
	}

	/**
	 * Gets the size of this multimap, that is, the number of key-value
	 * mappings.
	 * 
	 * @return The number of keys in the map.
	 */
	public int size() {
		return innerMap_.size();
	}

	/**
	 * Gets the total summed size of this multimap, that is, the sum of sizes
	 * for each value in the mapping.
	 * 
	 * @return The total number of values in the multimap.
	 */
	public int sizeTotal() {
		return values().size();
	}

	/**
	 * Gets every value present in the multimap.
	 * 
	 * @return All the values present in the multimap.
	 */
	public Collection<V> values() {
		Collection<V> values = null;
		if (collectionType_ == LIST_COLLECTION)
			values = new ArrayList<V>();
		else if (collectionType_ == SORTED_SET_COLLECTION)
			values = new TreeSet<V>();
		else if (collectionType_ == CONCURRENT_HASH_COLLECTION)
			values = Collections
					.newSetFromMap(new ConcurrentHashMap<V, Boolean>());
		for (Collection<V> valueCollections : valuesCollections()) {
			values.addAll(valueCollections);
		}
		return values;
	}

	/**
	 * Gets the lists of values in the middle level of the multimap.
	 * 
	 * @return All the lists containing the values.
	 */
	public Collection<Collection<V>> valuesCollections() {
		return innerMap_.values();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + collectionType_;
		result = prime * result
				+ ((comparator_ == null) ? 0 : comparator_.hashCode());
		result = prime * result
				+ ((innerMap_ == null) ? 0 : innerMap_.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiMap other = (MultiMap) obj;
		if (collectionType_ != other.collectionType_)
			return false;
		if (comparator_ == null) {
			if (other.comparator_ != null)
				return false;
		} else if (!comparator_.equals(other.comparator_))
			return false;
		if (innerMap_ == null) {
			if (other.innerMap_ != null)
				return false;
		} else if (!innerMap_.equals(other.innerMap_))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return innerMap_.toString();
	}
}