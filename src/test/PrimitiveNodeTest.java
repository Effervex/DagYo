package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import graph.core.PrimitiveNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrimitiveNodeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		PrimitiveNode node = PrimitiveNode.parseNode("'c'");
		assertEquals(node.getPrimitive(), 'c');
		node = PrimitiveNode.parseNode("true");
		assertEquals(node.getPrimitive(), true);
		node = PrimitiveNode.parseNode("FALSE");
		assertEquals(node.getPrimitive(), false);
		node = PrimitiveNode.parseNode("3");
		assertEquals(node.getPrimitive(), Short.parseShort("3"));
		node = PrimitiveNode.parseNode("128");
		assertEquals(node.getPrimitive(), Short.parseShort("128"));
		node = PrimitiveNode.parseNode("56873265");
		assertEquals(node.getPrimitive(), 56873265);
		node = PrimitiveNode.parseNode("56873547284632876");
		assertEquals(node.getPrimitive(), 56873547284632876l);
		node = PrimitiveNode.parseNode("658.487f");
		assertEquals(node.getPrimitive(), 658.487f);
		node = PrimitiveNode.parseNode("0.0d");
		assertEquals(node.getPrimitive(), 0.0d);
		node = PrimitiveNode.parseNode("test");
		assertNull(node);
	}

}
