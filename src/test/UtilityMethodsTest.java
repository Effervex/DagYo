/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.UtilityMethods;

public class UtilityMethodsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSplit() {
		String str = "chose to follow dominus";
		ArrayList<String> split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 4);
		assertEquals(split.get(0), "chose");
		assertEquals(split.get(1), "to");
		assertEquals(split.get(2), "follow");
		assertEquals(split.get(3), "dominus");

		str = " chose to ";
		split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 3);
		assertEquals(split.get(0), "");
		assertEquals(split.get(1), "chose");
		assertEquals(split.get(2), "to");
		
		str = "isa Dog (CatFn Tom)";
		split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 3);
		assertEquals(split.get(0), "isa");
		assertEquals(split.get(1), "Dog");
		assertEquals(split.get(2), "(CatFn Tom)");
		
		str = "isa Dog (CatFn (CombineFn T om))";
		split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 3);
		assertEquals(split.get(0), "isa");
		assertEquals(split.get(1), "Dog");
		assertEquals(split.get(2), "(CatFn (CombineFn T om))");
		
		str = "prettyString Dog \"dog dog\"";
		split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 3);
		assertEquals(split.get(0), "prettyString");
		assertEquals(split.get(1), "Dog");
		assertEquals(split.get(2), "\"dog dog\"");
		
		str = "pretty\\ String Dog \"dog dog\"";
		split = UtilityMethods.split(str, ' ');
		assertEquals(split.size(), 3);
		assertEquals(split.get(0), "pretty\\ String");
		assertEquals(split.get(1), "Dog");
		assertEquals(split.get(2), "\"dog dog\"");
	}

}
