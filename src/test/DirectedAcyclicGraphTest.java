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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.Node;
import graph.core.PrimitiveNode;
import graph.core.StringNode;

import java.io.File;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Sam Sarjant
 */
public class DirectedAcyclicGraphTest {
	private DirectedAcyclicGraph sut_;

	/**
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sut_ = new DirectedAcyclicGraph(new File("test"));
		sut_.clear();
	}

	@After
	public void tearDown() {
		sut_.clear();
	}

	/**
	 * Test method for
	 * {@link graph.core.DirectedAcyclicGraph#addNode(graph.core.DAGNode)}.
	 * 
	 * @throws NamingException
	 */
	@Test
	public void testAddNode() {
		Node node = sut_.findOrCreateNode("Test",
				new StringNode("TestCreator"), true);
		assertNotNull(node);
		assertEquals(node, sut_.getNodeByID(((DAGNode) node).getID()));
		assertTrue(node instanceof DAGNode);
		assertSame(node, sut_.findDAGNode("Test"));
		assertSame(node, sut_.findOrCreateNode("Test", null, true));
		assertSame(node, sut_.findOrCreateNode("Test", null, true));
		Node otherNode = sut_.findOrCreateNode("\"Test\"", new StringNode(
				"TestCreator"), true);
		assertNotSame(node, otherNode);
		assertTrue(otherNode instanceof StringNode);
		otherNode = sut_.findOrCreateNode("'true",
				new StringNode("TestCreator"), true);
		assertNotSame(node, otherNode);
		assertTrue(otherNode instanceof PrimitiveNode);
		assertTrue(((PrimitiveNode) otherNode).getPrimitive() == Boolean.TRUE);
	}

	/**
	 * Test method for {@link graph.core.DirectedAcyclicGraph#addEdge(DAGEdge)}.
	 * 
	 * @throws NamingException
	 */
	@Test
	public void testAddEdge() {
		Node isa = sut_.findOrCreateNode("isa", new StringNode("TestCreator"),
				true);
		Node cow = sut_.findOrCreateNode("Cow", new StringNode("TestCreator"),
				true);
		Node mammal = sut_.findOrCreateNode("Mammal", new StringNode(
				"TestCreator"), true);
		Edge edge = sut_.findOrCreateEdge(new Node[] { isa, cow, mammal },
				new StringNode("TestCreator"), true);
		assertNotNull(edge);
		assertSame(edge, sut_.getEdgeByID(((DAGEdge) edge).getID()));
		assertSame(edge, sut_.findEdge(isa, cow, mammal));
		assertSame(edge, sut_.findOrCreateEdge(new Node[] { isa, cow, mammal },
				new StringNode("TestCreator"), true));
		assertSame(edge, sut_.findOrCreateEdge(new Node[] { isa, cow, mammal },
				null, false));
		DAGNode bovine = (DAGNode) sut_.findOrCreateNode("Bovine", null, true);
		Edge otherEdge = sut_.findOrCreateEdge(new Node[] { isa, cow, bovine },
				new StringNode("TestCreator"), true);
		assertNotSame(edge, otherEdge);
	}

	@Test
	public void testRemoveNode() {
		DAGNode test = (DAGNode) sut_.findOrCreateNode("test", new StringNode(
				"TestCreator"), true);
		assertNotNull(sut_.getNodeByID(test.getID()));
		assertTrue(sut_.removeNode(test));
		assertNull(sut_.getNodeByID(test.getID()));
		assertFalse(sut_.removeNode(test));

		test = (DAGNode) sut_.findOrCreateNode("test", new StringNode(
				"TestCreator"), true);
		assertTrue(sut_.removeNode(test.getID()));
		assertFalse(sut_.removeNode(test.getID()));

		// Removing relevant edges
		test = (DAGNode) sut_.findOrCreateNode("test", new StringNode(
				"TestCreator"), true);
		Node isa = sut_.findOrCreateNode("isa", new StringNode("TestCreator"),
				true);
		Node dud = sut_.findOrCreateNode("dud", new StringNode("TestCreator"),
				true);
		Node foo = sut_.findOrCreateNode("foo", new StringNode("TestCreator"),
				true);
		Node bar = sut_.findOrCreateNode("bar", new StringNode("TestCreator"),
				true);
		sut_.findOrCreateEdge(new Node[] { isa, test, dud }, new StringNode(
				"TestCreator"), true);
		sut_.findOrCreateEdge(new Node[] { foo, bar }, new StringNode(
				"TestCreator"), true);
		assertEquals(sut_.getNumEdges(), 2);
		assertTrue(sut_.removeNode(test));
		assertEquals(sut_.getNumEdges(), 1);
	}

	@Test
	public void testRemoveEdge() {
		Node foo = sut_.findOrCreateNode("foo", new StringNode("TestCreator"),
				true);
		Node bar = sut_.findOrCreateNode("bar", new StringNode("TestCreator"),
				true);
		DAGEdge edge = (DAGEdge) sut_.findOrCreateEdge(new Node[] { foo, bar },
				new StringNode("TestCreator"), true);
		assertNotNull(sut_.getEdgeByID(edge.getID()));
		assertEquals(sut_.getNumNodes(), 2);
		assertTrue(sut_.removeEdge(edge));
		assertEquals(sut_.getNumNodes(), 2);
		assertNull(sut_.getEdgeByID(edge.getID()));
	}
}
