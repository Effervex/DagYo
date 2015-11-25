/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sam Sarjant - initial API and implementation
 ******************************************************************************/
package test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.collection.StringTrie;
import util.collection.TStringTrie;
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
		Collection<String> values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("A"));
		assertEquals(trie.calcDepth(), 0);

		key = toArray("101");
		assertTrue(trie.put(key, 0, "B"));
		values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("B"));
		assertEquals(trie.calcDepth(), 1);

		key = toArray("010");
		assertTrue(trie.put(key, 0, "C"));
		values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("C"));
		assertEquals(trie.calcDepth(), 2);

		key = toArray("010");
		assertTrue(trie.put(key, 0, "D"));
		values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 2);
		assertTrue(values.contains("C"));
		assertTrue(values.contains("D"));
		assertEquals(trie.calcDepth(), 2);

		key = toArray("000");
		assertTrue(trie.put(key, 0, "E"));
		values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("E"));
		assertEquals(trie.calcDepth(), 3);

		key = toArray("0");
		assertTrue(trie.put(key, 0, "F"));
		values = trie.getValue(key, 0, null, false);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("F"));
		assertEquals(trie.calcDepth(), 3);

		// Recursive
		values = trie.getValue(key, 0, null, true);
		assertTrue(values.size() == 5);
		assertTrue(values.contains("A"));
		assertTrue(values.contains("C"));
		assertTrue(values.contains("D"));
		assertTrue(values.contains("E"));
		assertTrue(values.contains("F"));
		assertEquals(trie.calcDepth(), 3);

		assertTrue(trie.remove(toArray("0"), 0, "F"));

	}

	@Test
	public void testStringTrie() {
		TStringTrie<String> strTrie = new TStringTrie<>();
		assertTrue(strTrie.put("cat", "Cat"));
		Collection<String> values = strTrie.getValue("cat", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));
		assertEquals(strTrie.calcDepth(), 0);

		values = strTrie.getValue("CAT", true, true);
		assertNotNull(values);
		assertTrue(values.isEmpty());

		values = strTrie.getValue("cat", false, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Cat"));

		values = strTrie.getValue("CAT", true, false);
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

		values = strTrie.getValue("catc", false, true);
		assertNotNull(values);
		assertTrue(values.isEmpty());

		values = strTrie.getValue("catc", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("ball"));

		assertTrue(strTrie.put("catcus", "typo"));
		values = strTrie.getValue("catcus", true, true);
		assertTrue(values.size() == 1);
		assertTrue(values.contains("typo"));
		assertEquals(strTrie.calcDepth(), 5);

		values = strTrie.getValue("cat", true, true);
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
		Collection<String> values = strTrie.getValue("FruitFn", false, true);
		assertEquals(values.size(), 1);
		assertTrue(values.contains("FruitFn"));
		assertEquals(strTrie.calcDepth(), 0);

		assertTrue(strTrie.put("Fruit", "Fruit"));
		values = strTrie.getValue("Fruit", false, true);
		assertEquals(values.size(), 1);
		assertTrue(values.contains("Fruit"));
		assertEquals(strTrie.calcDepth(), 6);
	}

	@Test
	public void testStress() {
		Random r = new Random(0);
		int numSamples = 100000;
		int sampleSize = 80;
		TStringTrie<String> st = new TStringTrie<>();
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numSamples; i++) {
			StringBuilder str = new StringBuilder();
			for (int j = 0; j < sampleSize; j++)
				str.append((char) ('A' + r.nextInt(58)));
			st.put(str.toString(), str.toString());
		}
		System.out.println("Time index: "
				+ String.format("%.2f",
						(System.currentTimeMillis() - startTime) / 1000f));

		startTime = System.currentTimeMillis();
		st.getValue("ac", true, false);
		System.out.println("Time search: "
				+ String.format("%.2f",
						(System.currentTimeMillis() - startTime) / 1000f));

		System.out.println("Mem: "
				+ String.format("%.2f",
						(Runtime.getRuntime().maxMemory() - Runtime
								.getRuntime().freeMemory()) / (1024 * 1024f))
				+ "MB");
	}

	private Integer[] toArray(String string) {
		Integer[] ints = new Integer[string.length()];
		for (int i = 0; i < ints.length; i++)
			ints[i] = Integer.parseInt(string.charAt(i) + "");
		return ints;
	}
}
