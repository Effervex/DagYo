/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.Node;
import graph.core.PrimitiveNode;
import graph.core.StringNode;
import graph.module.RelatedEdgeModule;

import java.io.File;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RelatedEdgeModuleTest {
	private RelatedEdgeModule sut_;
	private DirectedAcyclicGraph dag_;

	@Before
	public void setUp() throws Exception {
		dag_ = new DirectedAcyclicGraph(new File("test"));
		sut_ = (RelatedEdgeModule) dag_.getModule(RelatedEdgeModule.class);
		sut_.clear();
	}

	@After
	public void tearDown() throws Exception {
		sut_.clear();
	}

	@Test
	public void testExecute() {
		Node creator = new StringNode("TestCreator");
		DAGNode testNode = (DAGNode) dag_.findOrCreateNode("test", creator,
				true);
		DAGNode isa = (DAGNode) dag_.findOrCreateNode("isa", creator, true);
		DAGNode mammal = (DAGNode) dag_.findOrCreateNode("Mammal", creator,
				true);
		DAGEdge testEdge = (DAGEdge) dag_.findOrCreateEdge(creator, new Node[] {
				isa, testNode, mammal }, false);
		Collection<Edge> result = sut_.execute(testNode);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));

		DAGNode cowNode = (DAGNode) dag_.findOrCreateNode("Cow", creator, true);
		DAGEdge otherEdge = (DAGEdge) dag_.findOrCreateEdge(creator,
				new Node[] { isa, cowNode, mammal }, false);
		result = sut_.execute(testNode);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));

		result = sut_.execute(isa);
		assertEquals(result.size(), 2);
		assertTrue(result.contains(testEdge));
		assertTrue(result.contains(otherEdge));

		// Multiple arguments
		result = sut_.execute(isa, mammal);
		assertEquals(result.size(), 2);
		assertTrue(result.contains(testEdge));
		assertTrue(result.contains(otherEdge));

		result = sut_.execute(isa, testNode);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));

		result = sut_.execute(cowNode, testNode);
		assertEquals(result.size(), 0);

		// Non DAG Nodes
		DAGNode argIsa = (DAGNode) dag_.findOrCreateNode("argIsa", creator,
				true);
		DAGNode thing = (DAGNode) dag_.findOrCreateNode("Thing", creator, true);
		DAGNode collection = (DAGNode) dag_.findOrCreateNode("Collection",
				creator, true);
		DAGEdge isaEdge1 = (DAGEdge) dag_.findOrCreateEdge(creator, new Node[] {
				argIsa, isa, PrimitiveNode.parseNode("1"), thing }, false);
		DAGEdge isaEdge2 = (DAGEdge) dag_.findOrCreateEdge(creator, new Node[] {
				argIsa, isa, PrimitiveNode.parseNode("2"), collection }, false);

		result = sut_.execute(isa);
		assertEquals(result.size(), 4);
		assertTrue(result.contains(testEdge));
		assertTrue(result.contains(otherEdge));
		assertTrue(result.contains(isaEdge1));
		assertTrue(result.contains(isaEdge2));

		result = sut_.execute(isa, PrimitiveNode.parseNode("1"));
		assertEquals(result.size(), 1);
		assertTrue(result.contains(isaEdge1));
	}

	@Test
	public void testExecuteArgNum() {
		Node creator = new StringNode("TestCreator");
		DAGNode testNode = (DAGNode) dag_.findOrCreateNode("test", creator,
				true);
		DAGNode isa = (DAGNode) dag_.findOrCreateNode("isa", creator, true);
		DAGNode mammal = (DAGNode) dag_.findOrCreateNode("mammal", creator,
				true);
		DAGEdge testEdge = (DAGEdge) dag_.findOrCreateEdge(creator, new Node[] {
				isa, testNode, mammal }, false);
		Collection<Edge> result = sut_.execute(testNode, 2);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));
		result = sut_.execute(testNode, 1);
		assertEquals(result.size(), 0);

		DAGEdge otherEdge = (DAGEdge) dag_.findOrCreateEdge(creator,
				new Node[] { isa, mammal, testNode }, false);
		result = sut_.execute(testNode, 2);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));
		result = sut_.execute(testNode, 3);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(otherEdge));

		dag_.findOrCreateEdge(creator, new Node[] { isa, testNode,
				new StringNode("Test2") }, false);
		result = sut_.execute(testNode, 2, mammal);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(testEdge));
		result = sut_.execute(testNode, 2, mammal, 4);
		assertEquals(result.size(), 0);

		// Non DAG Nodes
		DAGNode argIsa = (DAGNode) dag_.findOrCreateNode("argIsa", creator,
				true);
		DAGNode thing = (DAGNode) dag_.findOrCreateNode("Thing", creator, true);
		DAGNode collection = (DAGNode) dag_.findOrCreateNode("Collection",
				creator, true);
		DAGEdge isaEdge1 = (DAGEdge) dag_.findOrCreateEdge(creator, new Node[] {
				argIsa, isa, PrimitiveNode.parseNode("1"), thing }, false);
		dag_.findOrCreateEdge(creator,
				new Node[] { argIsa, isa, PrimitiveNode.parseNode("2"),
						collection }, false);

		result = sut_.execute(isa, 2, thing, 4);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(isaEdge1));

		result = sut_.execute(isa, PrimitiveNode.parseNode("1"), 3);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(isaEdge1));

		DAGNode prettyString = (DAGNode) dag_.findOrCreateNode("prettyString",
				creator, true);
		DAGEdge stringEdge = (DAGEdge) dag_.findOrCreateEdge(creator,
				new Node[] { prettyString, mammal, new StringNode("Mammal") },
				false);

		result = sut_.execute(mammal, new StringNode("Mammal"), 3);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(stringEdge));

		// Remove args
		result = sut_.execute(mammal);
		assertEquals(result.size(), 3);
		assertTrue(result.contains(testEdge));
		assertTrue(result.contains(otherEdge));
		assertTrue(result.contains(stringEdge));

		result = sut_.execute(mammal, isa, -1);
		assertEquals(result.size(), 1);
		assertTrue(result.contains(stringEdge));
	}
}
