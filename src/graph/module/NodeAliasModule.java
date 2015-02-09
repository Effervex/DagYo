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
package graph.module;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import graph.core.StringNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;

import util.AliasedObject;
import util.collection.MergeSet;
import util.collection.StringTrie;

/**
 * The node alias module encodes the aliases for a node (be they from node name
 * or alias edges) into an efficient, searchable structure.
 * 
 * @author Sam Sarjant
 */
public class NodeAliasModule extends DAGModule<Collection<DAGNode>> implements
		AliasModule {
	private static final long serialVersionUID = 7451861373081932549L;
	public static final String ALIAS_PROP = "alias";
	private StringTrie<DAGNode> aliasTrie_ = new StringTrie<>();

	private String processAlias(String name) {
		name = name.replaceAll("\\s{2,}", " ");
		return name;
	}

	public boolean addAlias(DAGNode node, String alias) {
		if (alias.isEmpty())
			return false;
		aliasTrie_.put(processAlias(alias), node);
		return true;
	}

	@Override
	public boolean addEdge(DAGEdge edge) {
		if (edge.getProperty(ALIAS_PROP) != null) {
			Node[] edgeNodes = edge.getNodes();
			for (int i = 2; i < edgeNodes.length; i++) {
				if (edgeNodes[i] instanceof StringNode) {
					if (edgeNodes[1] instanceof DAGNode)
						addAlias((DAGNode) edgeNodes[1], edgeNodes[i].getName());
					else
						System.err
								.println("Alias edge defined by non-DAG node: "
										+ edge.toString());
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean addNode(DAGNode node) {
		if (!node.isAnonymous())
			addAlias(node, node.getName());
		return true;
	}

	@Override
	public void clear() {
		aliasTrie_.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeAliasModule other = (NodeAliasModule) obj;
		if (aliasTrie_ == null) {
			if (other.aliasTrie_ != null)
				return false;
		} else if (!aliasTrie_.equals(other.aliasTrie_))
			return false;
		return true;
	}

	@Override
	public Collection<DAGNode> execute(Object... args)
			throws IllegalArgumentException {
		if (args == null || args.length == 0)
			throw new IllegalArgumentException(
					"Requires at least 1 string argument.");
		String alias = (String) args[0];

		boolean caseSensitive = true;
		if (args.length >= 2)
			caseSensitive = (boolean) args[1];

		boolean exactString = true;
		if (args.length >= 3)
			exactString = (boolean) args[2];

		boolean onlyNodeNames = false;
		if (args.length >= 4)
			onlyNodeNames = (boolean) args[3];

		if (onlyNodeNames)
			return findNodeByName(alias, caseSensitive);
		else
			return findNodeByAlias(alias, caseSensitive, exactString, true);
	}

	@Override
	public Collection<AliasedObject<Character, DAGNode>> findAliasedNodes(
			String alias, boolean caseSensitive, boolean exactString) {
		Collection<AliasedObject<Character, DAGNode>> aliased = new MergeSet<>();
		aliasTrie_.getValue(ArrayUtils.toObject(alias.toCharArray()), 0,
				aliased, !exactString, caseSensitive);
		return aliased;
	}

	/**
	 * Find a node(s) by its alias or name.
	 * 
	 * @param alias
	 *            The alias to search with.
	 * @param caseSensitive
	 *            If the alias is case sensitive
	 * @param exactString
	 *            If the alias is exact or represents an prefix substring.
	 * @param excludeEphemeral
	 *            If ephemeral nodes are excluded from the results.
	 * @return All nodes indexed by the given alias.
	 */
	public Collection<DAGNode> findNodeByAlias(String alias,
			boolean caseSensitive, boolean exactString, boolean excludeEphemeral) {
		Collection<DAGNode> nodes = aliasTrie_.getValue(processAlias(alias),
				!exactString, caseSensitive);
		if (nodes == null)
			return new ArrayList<>(0);

		if (excludeEphemeral) {
			Collection<DAGNode> nonEphemeral = new ArrayList<DAGNode>(
					nodes.size());
			for (DAGNode n : nodes)
				if (n.getProperty(DirectedAcyclicGraph.EPHEMERAL_MARK) == null)
					nonEphemeral.add(n);
			nodes = nonEphemeral;
		}

		// Converting ID to node
		return nodes;
	}

	/**
	 * Finds a node(s) by its exact name (not alias).
	 * 
	 * @param nodeName
	 *            The name of the node.
	 * @param caseSensitive
	 *            If the search is case sensitive.
	 * @return All nodes with the same exact name.
	 */
	public Collection<DAGNode> findNodeByName(String nodeName,
			boolean caseSensitive) {
		Collection<DAGNode> aliasNodes = findNodeByAlias(
				processAlias(nodeName), caseSensitive, true, false);
		Collection<DAGNode> namedNodes = new HashSet<>(aliasNodes.size());
		for (DAGNode aliasNode : aliasNodes) {
			if (caseSensitive && aliasNode.getName().equals(nodeName)
					|| !caseSensitive
					&& aliasNode.getName().equalsIgnoreCase(nodeName))
				namedNodes.add(aliasNode);
		}
		return namedNodes;
	}

	@Override
	public Collection<DAGNode> findNodes(String alias, boolean caseSensitive,
			boolean exactString) {
		return execute(alias, caseSensitive, exactString);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aliasTrie_ == null) ? 0 : aliasTrie_.hashCode());
		return result;
	}

	@Override
	public boolean initialisationComplete(Collection<DAGNode> nodes,
			Collection<DAGEdge> edges, boolean forceRebuild) {
		if (!aliasTrie_.isEmpty() && !forceRebuild)
			return false;

		// Iterate through all nodes and edges, adding aliases
		System.out.print("Rebuilding alias trie... ");
		aliasTrie_.clear();
		defaultRebuild(nodes, true, edges, true);
		System.out.println("Done!");
		return true;
	}

	public boolean removeAlias(DAGNode node, String alias) {
		if (alias.isEmpty())
			return false;
		aliasTrie_.remove(processAlias(alias), node);
		return true;
	}

	@Override
	public boolean removeEdge(DAGEdge edge) {
		if (edge.getProperty(ALIAS_PROP) != null) {
			Node[] edgeNodes = edge.getNodes();
			boolean changed = false;
			for (int i = 2; i < edgeNodes.length; i++) {
				changed |= aliasTrie_.remove(
						processAlias(edgeNodes[i].getName()),
						(DAGNode) edgeNodes[1]);
			}
			return changed;
		}
		return false;
	}

	@Override
	public boolean removeNode(DAGNode node) {
		if (!node.isAnonymous())
			return aliasTrie_.remove(processAlias(node.getName()), node);
		return true;
	}

	@Override
	public boolean supportsEdge(DAGEdge edge) {
		return true;
	}

	@Override
	public boolean supportsNode(DAGNode node) {
		return true;
	}

	@Override
	public String toString() {
		return "Node Alias Module: " + aliasTrie_.toString();
	}
}
