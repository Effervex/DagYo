package test;

import static org.junit.Assert.*;
import graph.core.StringNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringNodeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStringNode() {
		StringNode strNode = new StringNode("\"US BANCORP \\\\DE\\\\\"");
		assertEquals(strNode.getName(), "US BANCORP \\\\DE\\\\");
		
		strNode = new StringNode("\"US BANCORP \\\\DE\\\\\\\"");
		assertEquals(strNode.getName(), "US BANCORP \\\\DE\\\\\\\"");
	}

}
