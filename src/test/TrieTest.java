/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.collection.StringTrie;
import util.collection.Trie;

public class TrieTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTrie() {
		Trie<Integer, String> trie = new Trie<>();
		Integer[] key = toArray("001");
		assertTrue(trie.put(key, 0, "A"));
		Set<String> values = trie.getValue(key, 0);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("A"));
		assertEquals(trie.calcDepth(), 0);

		key = toArray("101");
		assertTrue(trie.put(key, 0, "B"));
		values = trie.getValue(key, 0);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("B"));
		assertEquals(trie.calcDepth(), 1);

		key = toArray("010");
		assertTrue(trie.put(key, 0, "C"));
		values = trie.getValue(key, 0);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("C"));
		assertEquals(trie.calcDepth(), 2);

		key = toArray("010");
		assertTrue(trie.put(key, 0, "D"));
		values = trie.getValue(key, 0);
		assertTrue(values.size() == 2);
		assertTrue(values.contains("C"));
		assertTrue(values.contains("D"));
		assertEquals(trie.calcDepth(), 2);

		key = toArray("000");
		assertTrue(trie.put(key, 0, "E"));
		values = trie.getValue(key, 0);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("E"));
		assertEquals(trie.calcDepth(), 3);

		key = toArray("0");
		assertTrue(trie.put(key, 0, "F"));
		values = trie.getValue(key, 0);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("F"));
		assertEquals(trie.calcDepth(), 3);

		assertTrue(trie.remove(toArray("0"), 0, "F"));

	}

	@Test
	public void testStringTrie() {
		StringTrie<String> strTrie = new StringTrie<>();
		assertTrue(strTrie.put("cat", "Cat"));
		Set<String> values = strTrie.getValue("cat", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));
		assertEquals(strTrie.calcDepth(), 0);

		values = strTrie.getValue("CAT", true, true);
		assertTrue(values.isEmpty());

		values = strTrie.getValue("cat", true, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));

		values = strTrie.getValue("CAT", false, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));

		assertFalse(strTrie.put("cat", "Cat"));
		values = strTrie.getValue("cat", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));
		assertEquals(strTrie.calcDepth(), 0);

		assertTrue(strTrie.put("cat", "FELINE"));
		values = strTrie.getValue("cat", true, true);
		assertTrue(values.size() == 2);
		assertTrue(values.contains("Cat"));
		assertTrue(values.contains("FELINE"));
		assertEquals(strTrie.calcDepth(), 0);

		assertTrue(strTrie.put("catch", "ball"));
		values = strTrie.getValue("catch", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("ball"));
		assertEquals(strTrie.calcDepth(), 4);

		values = strTrie.getValue("catc", true, true);
		assertTrue(values.isEmpty());

		values = strTrie.getValue("catc", true, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("ball"));

		assertTrue(strTrie.put("catcus", "typo"));
		values = strTrie.getValue("catcus", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("typo"));
		assertEquals(strTrie.calcDepth(), 5);

		values = strTrie.getValue("cat", true, false);
		assertTrue(values.size() == 4);
		assertTrue(values.contains("Cat"));
		assertTrue(values.contains("FELINE"));
		assertTrue(values.contains("ball"));
		assertTrue(values.contains("typo"));
	}

	@Test
	public void testSpecial() {
		StringTrie<String> strTrie = new StringTrie<>();
		assertTrue(strTrie.put("FruitFn", "FruitFn"));
		Set<String> values = strTrie.getValue("FruitFn", true, true);
		assertEquals(values.size(), 1);
		assertTrue(values.contains("FruitFn"));
		assertEquals(strTrie.calcDepth(), 0);

		assertTrue(strTrie.put("Fruit", "Fruit"));
		values = strTrie.getValue("Fruit", true, true);
		assertEquals(values.size(), 1);
		assertTrue(values.contains("Fruit"));
		assertEquals(strTrie.calcDepth(), 6);
	}

	private Integer[] toArray(String string) {
		Integer[] ints = new Integer[string.length()];
		for (int i = 0; i < ints.length; i++)
			ints[i] = Integer.parseInt(string.charAt(i) + "");
		return ints;
	}
}
