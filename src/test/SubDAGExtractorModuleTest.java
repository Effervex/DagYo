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
import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.Node;
import graph.module.RelatedEdgeModule;
import graph.module.SubDAGExtractorModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubDAGExtractorModuleTest {
	private SubDAGExtractorModule sut_;
	private DirectedAcyclicGraph dag_;

	/**
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dag_ = new DirectedAcyclicGraph(new File("test"));
		sut_ = (SubDAGExtractorModule) dag_
				.getModule(SubDAGExtractorModule.class);
		sut_.clear();
	}

	@After
	public void tearDown() {
		sut_.clear();
	}

	@Test
	public void testExecute() throws IOException {
		String tag = "test";

		Node creator = dag_.findOrCreateNode("TestCreator", null, true);
		Node genls = dag_.findOrCreateNode("genls", creator, true);
		Node isa = dag_.findOrCreateNode("isa", creator, true);
		Node dog = dag_.findOrCreateNode("Dog", creator, true);
		Node canis = dag_.findOrCreateNode("CanisGenus", creator, true);
		Node fido = dag_.findOrCreateNode("Fido", creator, true);
		Node pet = dag_.findOrCreateNode("Pet", creator, true);
		dag_.findOrCreateEdge(new Node[] { genls, dog, canis }, creator, true);
		dag_.findOrCreateEdge(new Node[] { isa, fido, dog }, creator, true);
		dag_.findOrCreateEdge(new Node[] { isa, fido, pet }, creator, true);

		sut_.tagDAGObject((DAGNode) dog, tag);
		File folder = new File("testSubDAG");
		if (folder.exists())
			FileUtils.deleteDirectory(folder);
		folder.mkdir();
		assertTrue(sut_.extractSubDAG(folder, tag, 0));
		DirectedAcyclicGraph subDAG = new DirectedAcyclicGraph(folder);
		assertEquals(subDAG.getNodes().size(), 1);
		assertTrue(subDAG.findOrCreateNode("Dog", null, false) != null);
		assertEquals(subDAG.getEdges().size(), 0);

		if (folder.exists())
			FileUtils.deleteDirectory(folder);
		folder.mkdir();
		assertTrue(sut_.extractSubDAG(folder, tag, 1));
		subDAG = new DirectedAcyclicGraph(folder);
		assertEquals(subDAG.getNodes().size(), 5);
		assertTrue(subDAG.findOrCreateNode("Dog", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("CanisGenus", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Fido", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("isa", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("genls", null, false) != null);
		assertEquals(subDAG.getEdges().size(), 2);

		if (folder.exists())
			FileUtils.deleteDirectory(folder);
		folder.mkdir();
		assertTrue(sut_.extractSubDAG(folder, tag, 2));
		subDAG = new DirectedAcyclicGraph(folder);
		assertEquals(subDAG.getNodes().size(), 6);
		assertTrue(subDAG.findOrCreateNode("Dog", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("CanisGenus", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Fido", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Pet", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("isa", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("genls", null, false) != null);
		assertEquals(subDAG.getEdges().size(), 3);

		sut_.tagDAGObject((DAGNode) pet, tag);
		if (folder.exists())
			FileUtils.deleteDirectory(folder);
		folder.mkdir();
		assertTrue(sut_.extractSubDAG(folder, tag, 0));
		subDAG = new DirectedAcyclicGraph(folder);
		assertEquals(subDAG.getNodes().size(), 2);
		assertTrue(subDAG.findOrCreateNode("Dog", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Pet", null, false) != null);
		assertEquals(subDAG.getEdges().size(), 0);

		sut_.tagDAGObject((DAGNode) pet, tag);
		if (folder.exists())
			FileUtils.deleteDirectory(folder);
		folder.mkdir();
		assertTrue(sut_.extractSubDAG(folder, tag, 1));
		subDAG = new DirectedAcyclicGraph(folder);
		assertEquals(subDAG.getNodes().size(), 6);
		assertTrue(subDAG.findOrCreateNode("Dog", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("CanisGenus", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Fido", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("Pet", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("isa", null, false) != null);
		assertTrue(subDAG.findOrCreateNode("genls", null, false) != null);
		assertEquals(subDAG.getEdges().size(), 3);

		if (folder.exists())
			FileUtils.deleteDirectory(folder);
	}

	@Test
	public void testFindLinks() {
		RelatedEdgeModule relatedEdgeModule = (RelatedEdgeModule) dag_
				.getModule(RelatedEdgeModule.class);
		Collection<DAGNode> nodes = new HashSet<>();

		Node creator = dag_.findOrCreateNode("TestCreator", null, true);
		Node genls = dag_.findOrCreateNode("genls", creator, true);
		Node isa = dag_.findOrCreateNode("isa", creator, true);
		Node dog = dag_.findOrCreateNode("Dog", creator, true);
		Node canis = dag_.findOrCreateNode("CanisGenus", creator, true);
		Node fido = dag_.findOrCreateNode("Fido", creator, true);
		Node pet = dag_.findOrCreateNode("Pet", creator, true);
		Edge dogCanis = dag_.findOrCreateEdge(new Node[] { genls, dog, canis },
				creator, true);
		Edge fidoDog = dag_.findOrCreateEdge(new Node[] { isa, fido, dog },
				creator, true);
		Edge fidoPet = dag_.findOrCreateEdge(new Node[] { isa, fido, pet },
				creator, true);

		nodes.clear();
		nodes.add((DAGNode) dog);
		Collection<DAGEdge> linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 0);

		nodes.clear();
		nodes.add((DAGNode) dog);
		nodes.add((DAGNode) fido);
		linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 1);
		assertTrue(linkedEdges.contains(fidoDog));

		nodes.clear();
		nodes.add((DAGNode) canis);
		nodes.add((DAGNode) fido);
		linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 0);

		nodes.clear();
		nodes.add((DAGNode) canis);
		nodes.add((DAGNode) dog);
		nodes.add((DAGNode) fido);
		linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 2);
		assertTrue(linkedEdges.contains(fidoDog));
		assertTrue(linkedEdges.contains(dogCanis));

		nodes.clear();
		nodes.add((DAGNode) canis);
		nodes.add((DAGNode) dog);
		nodes.add((DAGNode) fido);
		nodes.add((DAGNode) pet);
		linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 3);
		assertTrue(linkedEdges.contains(fidoDog));
		assertTrue(linkedEdges.contains(fidoPet));
		assertTrue(linkedEdges.contains(dogCanis));

		// Predicate addition
		Node canBe = dag_.findOrCreateNode("canBe", creator, true);
		Edge canisPet = dag_.findOrCreateEdge(new Node[] { canBe, canis, pet },
				creator, true);
		nodes.clear();
		nodes.add((DAGNode) canis);
		nodes.add((DAGNode) pet);
		linkedEdges = sut_.incorporateNewAndLinkEdges(
				new HashSet<DAGNode>(), nodes, relatedEdgeModule);
		assertEquals(linkedEdges.size(), 1);
		assertTrue(linkedEdges.contains(canisPet));
		assertEquals(nodes.size(), 1);
		assertTrue(nodes.contains(canBe));
	}

	@Test
	public void testFollowEdges() {
		RelatedEdgeModule relatedEdgeModule = (RelatedEdgeModule) dag_
				.getModule(RelatedEdgeModule.class);
		Collection<DAGNode> nodes = new HashSet<>();
		Collection<DAGNode> existingNodes = new ArrayList<>();

		Node creator = dag_.findOrCreateNode("TestCreator", null, true);
		Node genls = dag_.findOrCreateNode("genls", creator, true);
		Node isa = dag_.findOrCreateNode("isa", creator, true);
		Node dog = dag_.findOrCreateNode("Dog", creator, true);
		Node canis = dag_.findOrCreateNode("CanisGenus", creator, true);
		Node fido = dag_.findOrCreateNode("Fido", creator, true);
		Node pet = dag_.findOrCreateNode("Pet", creator, true);
		dag_.findOrCreateEdge(new Node[] { genls, dog, canis }, creator, true);
		dag_.findOrCreateEdge(new Node[] { isa, fido, dog }, creator, true);
		dag_.findOrCreateEdge(new Node[] { isa, fido, pet }, creator, true);

		nodes.clear();
		nodes.add((DAGNode) dog);
		sut_.findLinks(nodes, existingNodes, 0, relatedEdgeModule);
		assertEquals(nodes.size(), 1);
		assertTrue(nodes.contains(dog));

		nodes.clear();
		nodes.add((DAGNode) dog);
		sut_.findLinks(nodes, existingNodes, 1, relatedEdgeModule);
		assertEquals(nodes.size(), 3);
		assertTrue(nodes.contains(dog));
		assertTrue(nodes.contains(fido));
		assertTrue(nodes.contains(canis));

		nodes.clear();
		nodes.add((DAGNode) fido);
		sut_.findLinks(nodes, existingNodes, 1, relatedEdgeModule);
		assertEquals(nodes.size(), 3);
		assertTrue(nodes.contains(dog));
		assertTrue(nodes.contains(fido));
		assertTrue(nodes.contains(pet));

		nodes.clear();
		nodes.add((DAGNode) dog);
		sut_.findLinks(nodes, existingNodes, 2, relatedEdgeModule);
		assertEquals(nodes.size(), 4);
		assertTrue(nodes.contains(dog));
		assertTrue(nodes.contains(fido));
		assertTrue(nodes.contains(canis));
		assertTrue(nodes.contains(pet));

		nodes.clear();
		nodes.add((DAGNode) canis);
		sut_.findLinks(nodes, existingNodes, 2, relatedEdgeModule);
		assertEquals(nodes.size(), 3);
		assertTrue(nodes.contains(canis));
		assertTrue(nodes.contains(dog));
		assertTrue(nodes.contains(fido));
	}

}
