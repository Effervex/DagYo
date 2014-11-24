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
package graph.module.cli;

import graph.core.DAGNode;
import graph.core.cli.CollectionCommand;
import graph.core.cli.DAGPortHandler;
import graph.core.cli.comparator.AliasedNodesComparator;
import graph.module.AliasModule;
import graph.module.ModuleException;
import graph.module.NodeAliasModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import util.AliasedObject;
import util.UtilityMethods;

/**
 * Finds a node by an alias string. This alias could be the node name or an
 * alias encoded by an edge.
 * 
 * @author Sam Sarjant
 */
public class FindNodeByAliasCommand extends CollectionCommand {
	private static final int ALIASED_OBJECT = 2;
	private static final int DAG_NODE = 1;

	@Override
	public String helpText() {
		return "{0} alias [caseSensitive] [exactString] [showMatch] : "
				+ "Return all nodes with a matching alias (optionally quoted), with "
				+ "optional parameters [caseSensitive] (T/F), "
				+ "[exactString] (T/F), and [showMatch] (T/F) "
				+ "to perform case-sensitive "
				+ "search (default true), exact/prefix string "
				+ "searching (default true), or show the matching "
				+ "alias (default false).";
	}

	@Override
	public String shortDescription() {
		return "Finds all nodes by their name/alias.";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void executeImpl() {
		super.executeImpl();
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Collection<Object> nodes = findNodes(dagHandler);
		if (nodes == null)
			return;

		// TODO See if the results can be sorted categorically (identical node
		// name, then identical alias name, then non-exact names)
		Comparator<Object> comparator = dagHandler.getComparator();
		boolean aliasSort = comparator != null && comparator instanceof AliasedNodesComparator;
		if (aliasSort) {
			List<Object> listNodes = new ArrayList<>(nodes);
			ArrayList<String> split = UtilityMethods.split(data, ' ');
			Collections.sort(listNodes, new AliasedNodesComparator(dagHandler, split.get(0)));
			nodes = listNodes;
		}
		nodes = dagHandler.postProcess(nodes, rangeStart_, rangeEnd_, !aliasSort);

		print(nodes.size() + "|");
		int objectIndicator = 0;
		for (Object n : nodes) {
			if (objectIndicator == 0) {
				if (n instanceof DAGNode)
					objectIndicator = DAG_NODE;
				if (n instanceof AliasedObject)
					objectIndicator = ALIASED_OBJECT;
			}

			if (objectIndicator == DAG_NODE)
				print(dagHandler.textIDObject((DAGNode) n) + "|");
			else if (objectIndicator == ALIASED_OBJECT) {
				printAliased((AliasedObject<Character, DAGNode>) n, dagHandler);
			}
		}
		print("\n");
	}

	protected void printAliased(AliasedObject<Character, DAGNode> ao,
			DAGPortHandler dagHandler) {
		String alias = new String(ArrayUtils.toPrimitive(ao.alias_));
		print(dagHandler.textIDObject(ao.object_) + ",\"" + alias + "\"|");
	}

	protected Collection<Object> findNodes(DAGPortHandler dagHandler) {
		Collection<AliasModule> aliasModules = null;
		try {
			aliasModules = getAllAliasModules(dagHandler);
		} catch (ModuleException me) {
			print(me.getMessage() + "\n");
			return null;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		String alias = split.get(0);
		if (alias.matches(DAGNode.QUOTED_NAME.pattern()))
			alias = UtilityMethods.shrinkString(alias, 1);
		boolean caseSensitive = true;
		if (split.size() >= 2 && split.get(1).equals("F"))
			caseSensitive = false;
		boolean exactString = true;
		if (split.size() >= 3 && split.get(2).equals("F"))
			exactString = false;
		boolean aliasText = false;
		if (split.size() >= 4 && split.get(3).equals("T"))
			aliasText = true;

		Collection<Object> nodes = new TreeSet<>();
		if (!aliasText) {
			for (AliasModule aliasModule : aliasModules) {
				nodes.addAll(aliasModule.findNodes(alias, caseSensitive,
						exactString));
			}

		} else {
			for (AliasModule aliasModule : aliasModules) {
				nodes.addAll(aliasModule.findAliasedNodes(alias, caseSensitive,
						exactString));
			}
		}
		return nodes;
	}

	protected Collection<AliasModule> getAllAliasModules(
			DAGPortHandler dagHandler) {
		Collection<AliasModule> aliasModules = new ArrayList<>();

		NodeAliasModule aliasModule = (NodeAliasModule) dagHandler.getDAG()
				.getModule(NodeAliasModule.class);
		if (aliasModule == null)
			throw new ModuleException(
					"Node Alias module is not in use for this DAG.");
		aliasModules.add(aliasModule);

		return aliasModules;
	}
}
