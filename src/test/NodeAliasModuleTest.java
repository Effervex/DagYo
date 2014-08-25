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
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.StringNode;
import graph.module.NodeAliasModule;

import java.io.File;
import java.util.Collection;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.AliasedObject;

public class NodeAliasModuleTest {
	private NodeAliasModule sut_;
	private DirectedAcyclicGraph dag_;

	/**
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dag_ = new DirectedAcyclicGraph(new File("test"), null, null);
		sut_ = (NodeAliasModule) dag_.getModule(NodeAliasModule.class);
		sut_.clear();
	}

	@After
	public void tearDown() {
		sut_.clear();
	}

	/**
	 * Test method for
	 * {@link graph.core.DirectedAcyclicGraph#findNodeByName(java.lang.String, boolean)}
	 * .
	 * 
	 * @throws NamingException
	 */
	@Test
	public void testFindNodeByName() {
		dag_.findOrCreateNode("Test", new StringNode("TestCreator"), true);
		Collection<DAGNode> result = sut_.findNodeByName("Test", true);
		assertEquals(result.size(), 1);
		result = sut_.findNodeByName("test", true);
		assertEquals(result.size(), 0);
		result = sut_.findNodeByName("test", false);
		assertEquals(result.size(), 1);

		dag_.findOrCreateNode("Test", new StringNode("TestCreator"), true);
		result = sut_.findNodeByName("Test", true);
		assertEquals(result.size(), 1);
		dag_.findOrCreateNode("Pants", new StringNode("TestCreator"), true);
		result = sut_.findNodeByName("Pants", true);
		assertEquals(result.size(), 1);
		dag_.findOrCreateNode("Pants", new StringNode("TestCreator"), true);
		result = sut_.findNodeByName("Pants", true);
		assertEquals(result.size(), 1);
	}

	// TODO @Test
	// public void testRemoveEdge() {
	// DAGEdge aliasEdge = new AliasEdge(new DAGNode("dog"), new StringNode(
	// "Dog"), new StringNode("Canine"));
	// dag_.findOrCreateEdge(new StringNode("TestCreator"), alias, dogStr,
	// canine);
	// dag_.addEdge(aliasEdge);
	// assertEquals(sut_.findNodeByAlias("Dog", true, true).size(), 1);
	// dag_.removeEdge(aliasEdge);
	// assertEquals(sut_.findNodeByAlias("Dog", true, true).size(), 0);
	// }

	/**
	 * Test method for
	 * {@link graph.core.DirectedAcyclicGraph#findNodeByAlias(java.lang.String, boolean, boolean, boolean)}
	 * .
	 * 
	 * @throws NamingException
	 */
	@Test
	public void testFindNodeByAlias() {
		dag_.findOrCreateNode("Test", new StringNode("TestCreator"), true);
		Collection<DAGNode> result = sut_.findNodeByAlias("Test", true, true,
				true);
		assertEquals(result.size(), 1);
		result = sut_.findNodeByAlias("Tes", true, false, true);
		assertEquals(result.size(), 1);
		result = sut_.findNodeByAlias("tes", true, false, true);
		assertEquals(result.size(), 0);
		result = sut_.findNodeByAlias("tes", false, false, true);
		assertEquals(result.size(), 1);

		dag_.findOrCreateNode("FruitFn", null, true);
		dag_.findOrCreateNode("Fruit", null, true);
		result = sut_.findNodeByAlias("Fruit", true, false, true);
		assertEquals(result.size(), 2);
		result = sut_.findNodeByAlias("Fruit", true, true, true);
		assertEquals(result.size(), 1);
	}

	@Test
	public void testFindNode() {
		dag_.findOrCreateNode("Test", new StringNode("TestCreator"), true);
		Collection<AliasedObject<Character, DAGNode>> aliased = sut_
				.findAliasedNodes("Test", true, true);
		assertEquals(aliased.size(), 1);
	}
}
