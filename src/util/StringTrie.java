package util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A wrapper for tries indexed by string. Performs special techniques for
 * case-insensitivity.
 * 
 * @author Sam Sarjant
 */
public class StringTrie<T extends Serializable> extends Trie<Character, T> {
	private static final long serialVersionUID = 3197350529344780370L;
	/** The minimum number of characters required for substring searching. */
	private static final int MIN_SUBSTRING = 3;

	/**
	 * Recurses through the remainder of the subtrie.
	 * 
	 * @param subTrie
	 *            The trie to recurse all results from.
	 * @return A set of all elements from the subtrie.
	 */
	private Set<T> recurseAllSubResults() {
		if (subTrie_ == null)
			return leafVal_;

		Set<T> union = new HashSet<>();
		if (leafVal_ != null)
			union.addAll(leafVal_);
		for (Character keyChar : subTrie_.keySet()) {
			Set<T> subVals = ((StringTrie<T>) subTrie_.get(keyChar))
					.recurseAllSubResults();
			if (subVals != null)
				union.addAll(subVals);
		}
		return union;
	}

	/**
	 * Recursively calls on getValue for sub StringTries.
	 * 
	 * @param keyChar
	 *            The character as key.
	 * @param key
	 *            The key array.
	 * @param index
	 *            The key index in the array.
	 * @param caseSensitive
	 *            If the search is case sensitive.
	 * @param exactString
	 *            If the search is for an exact string.
	 * @param result
	 *            The results to add to.
	 */
	private void recurseGetValue(char keyChar, Character[] key, int index,
			boolean caseSensitive, boolean exactString, Set<T> result) {
		if (subTrie_.containsKey(keyChar)) {
			StringTrie<T> subTrie = (StringTrie<T>) subTrie_.get(keyChar);
			Set<T> subResults = subTrie.getValue(key, index, caseSensitive,
					exactString);
			if (subResults != null)
				result.addAll(subResults);
		}
	}

	@Override
	protected Trie<Character, T> newInstance() {
		return new StringTrie<>();
	}

	/**
	 * Special recursive method for dealing with substrings and case
	 * insensitivity.
	 * 
	 * @param key
	 *            The character array.
	 * @param index
	 *            The current index.
	 * @param caseSensitive
	 *            If the search is case sensitive.
	 * @param exactString
	 *            If the search is for a substring.
	 * @return The set of elements for the key.
	 */
	public Set<T> getValue(Character[] key, int index, boolean caseSensitive,
			boolean exactString) {
		if (key.length < MIN_SUBSTRING)
			exactString = true;
		if (index == key.length) {
			if (!exactString) {
				return recurseAllSubResults();
			} else if (shortenedBranch_ == null)
				return leafVal_;
		}
		Set<T> result = new TreeSet<>();
		if (subTrie_ == null) {
			if (shortenedBranch_ != null
					&& caseEquals(key, index, caseSensitive, exactString))
				return leafVal_;
			return result;
		}

		recurseGetValue(key[index], key, index + 1, caseSensitive, exactString,
				result);

		if (!caseSensitive) {
			char oppChar = key[index];
			if (Character.isUpperCase(key[index]))
				oppChar = Character.toLowerCase(key[index]);
			if (Character.isLowerCase(key[index]))
				oppChar = Character.toUpperCase(key[index]);

			if (oppChar != key[index])
				recurseGetValue(oppChar, key, index + 1, caseSensitive,
						exactString, result);
		}
		return result;
	}

	/**
	 * If the current key equals the shortened branch.
	 * 
	 * @param key
	 *            The key to check.
	 * @param index
	 *            The current index of the key
	 * @param caseSensitive
	 *            If the check is case sensitive.
	 * @param exactString
	 *            If the check is an exact string.
	 * @return True if the key matches the shortened branch.
	 */
	private boolean caseEquals(Character[] key, int index,
			boolean caseSensitive, boolean exactString) {
		int i = 0;
		for (; index < key.length; index++) {
			Character keyChar = (caseSensitive) ? key[index] : Character
					.toLowerCase(key[index]);
			if (i >= shortenedBranch_.length)
				return false;
			Character branchChar = (caseSensitive) ? shortenedBranch_[i]
					: Character.toLowerCase(shortenedBranch_[i]);
			if (!keyChar.equals(branchChar))
				return false;
			i++;
		}
		
		// Must match exactly if exactString
		if (exactString && i < shortenedBranch_.length)
			return false;
		
		return true;
	}

	/**
	 * Gets the set of all elements indexed by a given string. The string may be
	 * a substring, and does not have to be case sensitive.
	 * 
	 * @param str
	 *            The string to search with.
	 * @param caseSensitive
	 *            If the search is case-sensitive.
	 * @param exactString
	 *            If the string is an exact string (not substring).
	 */
	public Set<T> getValue(String str, boolean caseSensitive,
			boolean exactString) {
		Character[] charName = ArrayUtils.toObject(str.toCharArray());
		return getValue(charName, 0, caseSensitive, exactString);
	}

	/**
	 * A shortcut for putting an element into the character-indexed trie.
	 * 
	 * @param str
	 *            The string as the key.
	 * @param element
	 *            The element to put in the trie.
	 * @return The set of all elements indexed by the same key.
	 */
	public boolean put(String str, T element) {
		Character[] charName = ArrayUtils.toObject(str.toCharArray());
		return put(charName, 0, element);
	}

	/**
	 * A wrapper for element removal directed by string.
	 * 
	 * @param str
	 *            The string key.
	 * @param element
	 *            The element being removed.
	 * @return True if the trie changed fundamentally.
	 */
	public boolean remove(String str, T element) {
		Character[] charName = ArrayUtils.toObject(str.toCharArray());
		return remove(charName, 0, element);
	}
}
