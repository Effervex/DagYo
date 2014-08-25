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

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import graph.core.StringNode;

public class SerialisationTest {
	public static void main(String[] args) {
		File file = new File("test");
		file.mkdir();
		DirectedAcyclicGraph dag = new DirectedAcyclicGraph(file, null, null);
		int numNodes = 2200000;
		Random random = new Random();

		// Add a bunch of nodes
		Collection<DAGNode> nodes = new HashSet<>(numNodes);
		DAGNode creator = (DAGNode) dag.findOrCreateNode("Creator", null, true);
		System.out.print("Adding nodes");
		for (int i = 0; i < numNodes; i++) {
			String name = generateRandomString(random, 10);
			nodes.add((DAGNode) dag.findOrCreateNode(name, creator, true));

			if ((i % 100000) == 0)
				System.out.print(".");
		}
		numNodes = nodes.size();
		DAGNode[] nodeArray = nodes.toArray(new DAGNode[numNodes]);
		System.out.println();

		// Add a bunch of edges
		System.out.print("Adding edges");
		for (int i = 0; i < numNodes; i++) {
			DAGNode coreNode = nodeArray[i];
			int numEdges = 6 + random.nextInt(9);
			for (int j = 0; j < numEdges; j++) {
				// Limited predicates...
				DAGNode predicate = nodeArray[random.nextInt(numNodes / 100)];

				Node lastArg = null;
				if (random.nextDouble() < .2) {
					// String edge
					lastArg = new StringNode(generateRandomString(random,
							5 + random.nextInt(100)));
				} else
					lastArg = nodeArray[random.nextInt(numNodes)];
				Node[] edgeNodes = new Node[] { predicate, coreNode, lastArg };
				dag.findOrCreateEdge(edgeNodes, creator, true);
			}

			if ((i % 100000) == 0)
				System.out.print(".");
		}

		dag.saveState();
	}

	private static String generateRandomString(Random random, int numChars) {
		StringBuilder name = new StringBuilder();
		for (int j = 0; j < numChars; j++) {
			if (random.nextBoolean())
				name.append((char) ('A' + random.nextInt(26)));
			else
				name.append((char) ('a' + random.nextInt(26)));
		}
		return name.toString();
	}
}
