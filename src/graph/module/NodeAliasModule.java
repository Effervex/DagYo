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

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.StringNode;
import graph.edge.properties.Alias;
import graph.module.cli.AliasModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;

import util.AliasedObject;
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
	/** The Long actually represents a DAGNode ID. */
	private StringTrie<DAGNode> aliasTrie_ = new StringTrie<>();

	private String processAlias(String name) {
		name = name.replaceAll("\\s{2,}", " ");
		return name;
	}

	public boolean addAlias(DAGNode node, String alias) {
		aliasTrie_.put(processAlias(alias), node);
		return true;
	}

	@Override
	public boolean addEdge(Edge edge) {
		if (edge instanceof Alias) {
			Alias aliasEdge = (Alias) edge;
			for (StringNode alias : aliasEdge.getAliases())
				aliasTrie_.put(processAlias(alias.getName()),
						aliasEdge.getNode());
			return true;
		}
		return false;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aliasTrie_ == null) ? 0 : aliasTrie_.hashCode());
		return result;
	}

	@Override
	public boolean removeEdge(Edge edge) {
		if (edge instanceof Alias) {
			Alias aliasEdge = (Alias) edge;
			boolean changed = false;
			for (StringNode alias : aliasEdge.getAliases())
				changed |= aliasTrie_.remove(processAlias(alias.getName()),
						aliasEdge.getNode());
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
	public String toString() {
		return aliasTrie_.toString();
	}

	@Override
	public Collection<DAGNode> findNodes(String alias, boolean caseSensitive,
			boolean exactString) {
		return execute(alias, caseSensitive, exactString);
	}

	@Override
	public Collection<AliasedObject<Character, DAGNode>> findAliasedNodes(
			String alias, boolean caseSensitive, boolean exactString) {
		Collection<AliasedObject<Character, DAGNode>> aliased = new HashSet<>();
		aliasTrie_.getValue(ArrayUtils.toObject(alias.toCharArray()), 0,
				aliased, !exactString, caseSensitive);
		return aliased;
	}
}
