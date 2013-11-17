package util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A generic trie implementation.
 * 
 * @author Sam Sarjant
 */
public class Trie<E extends Serializable, V extends Serializable> implements
		Serializable {
	private static final long serialVersionUID = -297022640412951452L;
	/** The value(s) at this node. */
	protected Set<V> leafVal_;
	/** The recursive sub-trie. */
	protected Map<E, Trie<E, V>> subTrie_;
	/** A variable for keeping branches minimal length. */
	protected E[] shortenedBranch_;
	/** The number of times the key has been used to put values in. */
	private int keyCount_;

	public Trie() {
	}

	private boolean addValue(V value) {
		if (leafVal_ == null)
			leafVal_ = new HashSet<>();
		return leafVal_.add(value);
	}

	private Trie<E, V> getSubTrie(E edge) {
		if (subTrie_ == null)
			subTrie_ = new HashMap<>();
		Trie<E, V> subTrie = subTrie_.get(edge);
		if (subTrie == null) {
			subTrie = newInstance();
			subTrie_.put(edge, subTrie);
		}
		return subTrie;
	}

	/**
	 * Creates a new instance of the Trie. This method is used to ensure
	 * subtries are of the same class as the super trie.
	 * 
	 * @return The new Trie.
	 */
	protected Trie<E, V> newInstance() {
		return new Trie<>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + keyCount_;
		result = prime * result
				+ ((leafVal_ == null) ? 0 : leafVal_.hashCode());
		result = prime * result + Arrays.hashCode(shortenedBranch_);
		result = prime * result
				+ ((subTrie_ == null) ? 0 : subTrie_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Trie other = (Trie) obj;
		if (keyCount_ != other.keyCount_)
			return false;
		if (leafVal_ == null) {
			if (other.leafVal_ != null)
				return false;
		} else if (!leafVal_.equals(other.leafVal_))
			return false;
		if (!Arrays.equals(shortenedBranch_, other.shortenedBranch_))
			return false;
		if (subTrie_ == null) {
			if (other.subTrie_ != null)
				return false;
		} else if (!subTrie_.equals(other.subTrie_))
			return false;
		return true;
	}

	public Set<V> getValue(E[] key, int index) {
		if (shortenedBranch_ != null) {
			if (Arrays.equals(shortenedBranch_,
					Arrays.copyOfRange(key, index, key.length)))
				return leafVal_;
			return null;
		}
		if (index == key.length)
			return leafVal_;
		if (subTrie_ == null)
			return null;
		if (subTrie_.containsKey(key[index])) {
			return subTrie_.get(key[index]).getValue(key, index + 1);
		}
		return null;
	}

	public boolean isEmpty() {
		return subTrie_.isEmpty();
	}

	public boolean put(E[] key, int index, V value) {
		// Only create branches when necessary
		if (subTrie_ == null && index < key.length) {
			if (shortenedBranch_ == null && leafVal_ == null) {
				// Shorten the branch
				shortenedBranch_ = Arrays.copyOfRange(key, index, key.length);
				if (addValue(value)) {
					keyCount_++;
					return true;
				}
				return false;
			} else if (Arrays.equals(shortenedBranch_,
					Arrays.copyOfRange(key, index, key.length))) {
				// Add another value to the short branch
				if (addValue(value)) {
					keyCount_++;
					return true;
				}
				return false;
			}
		}

		if (shortenedBranch_ != null) {
			// Split the short leaf into two branches
			Trie<E, V> subTrie = getSubTrie(shortenedBranch_[0]);
			if (shortenedBranch_.length > 1)
				subTrie.shortenedBranch_ = Arrays.copyOfRange(shortenedBranch_,
						1, shortenedBranch_.length);
			subTrie.leafVal_ = leafVal_;
			subTrie.keyCount_ = keyCount_;
			shortenedBranch_ = null;
			leafVal_ = null;
		}

		if (index == key.length) {
			return addValue(value);
		} else {
			Trie<E, V> subTrie = getSubTrie(key[index]);
			boolean result = subTrie.put(key, index + 1, value);
			if (result)
				keyCount_++;
			return result;
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Trie - ");
		boolean subTrie = false;
		if (subTrie_ != null && !subTrie_.isEmpty()) {
			buffer.append("Keys: " + subTrie_.size() + ", Values: " + keyCount_);
			subTrie = true;
		}
		if (leafVal_ != null) {
			if (subTrie)
				buffer.append(", ");
			if (shortenedBranch_ != null)
				buffer.append(Arrays.toString(shortenedBranch_));
			buffer.append(leafVal_.toString());
		}
		return buffer.toString();
	}

	public int calcDepth() {
		if (subTrie_ == null)
			return 0;
		int maxDepth = 0;
		for (Trie<E, V> subTrie : subTrie_.values())
			maxDepth = Math.max(maxDepth, subTrie.calcDepth() + 1);
		return maxDepth;
	}

	public void clear() {
		subTrie_ = null;
	}

	public boolean remove(E[] key, int index, V value) {
		if (shortenedBranch_ != null) {
			if (Arrays.equals(shortenedBranch_,
					Arrays.copyOfRange(key, index, key.length))) {
				boolean result = leafVal_.remove(value);
				if (leafVal_.isEmpty())
					shortenedBranch_ = null;
				return result;
			}
			return false;
		}
		if (index == key.length) {
			return leafVal_.remove(value);
		} else {
			Trie<E, V> subTrie = subTrie_.get(key[index]);
			if (subTrie == null)
				return false;

			if (subTrie.remove(key, index + 1, value)) {
				keyCount_--;
				if (keyCount_ == 0) {
					subTrie_ = null;
					shortenedBranch_ = null;
				}
				return true;
			}
			return false;
		}
	}

	public void printOrdered() {
		recursePrint("");
	}

	private void recursePrint(String keyStr) {
		StringBuffer buffer = new StringBuffer();
		if (shortenedBranch_ != null) {
			for (E e : shortenedBranch_)
				buffer.append(e.toString());
		}
		if (leafVal_ != null)
			System.out.println(keyStr + buffer.toString() + ": "
					+ leafVal_.toString());
		if (subTrie_ != null) {
			Object[] array = new Object[subTrie_.size()];
			subTrie_.keySet().toArray(array);
			Arrays.sort(array);
			for (Object a : array) {
				subTrie_.get(a).recursePrint(keyStr + a.toString());
			}
		}
	}
}
