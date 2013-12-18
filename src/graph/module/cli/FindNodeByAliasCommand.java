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
import graph.module.DAGModule;
import graph.module.ModuleException;
import graph.module.NodeAliasModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import util.UtilityMethods;

/**
 * Finds a node by an alias string. This alias could be the node name or an
 * alias encoded by an edge.
 * 
 * @author Sam Sarjant
 */
public class FindNodeByAliasCommand extends CollectionCommand {
	@Override
	public String helpText() {
		return "{0} alias [caseSensitive] [exactString] : "
				+ "Return all nodes with a matching alias (optionally quoted), with "
				+ "optional parameters [caseSensitive] (T/F) and "
				+ "[exactString] (T/F) to perform case-sensitive "
				+ "search or exact/prefix string search "
				+ "(defaults true for each).";
	}

	@Override
	public String shortDescription() {
		return "Finds all nodes by their name/alias.";
	}

	@Override
	protected void executeImpl() {
		super.executeImpl();
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Collection<DAGModule<Collection<DAGNode>>> aliasModules = null;
		try {
			aliasModules = getAllAliasModules(dagHandler);
		} catch (ModuleException me) {
			print(me.getMessage() + "\n");
			return;
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

		Collection<DAGNode> nodes = new TreeSet<>();
		for (DAGModule<Collection<DAGNode>> aliasModule : aliasModules)
			nodes.addAll(aliasModule.execute(alias, caseSensitive, exactString));
		nodes = dagHandler.sort(nodes, rangeStart_, rangeEnd_);

		print(nodes.size() + "|");
		for (DAGNode n : nodes)
			print(dagHandler.textIDObject(n) + "|");
		print("\n");
	}

	protected Collection<DAGModule<Collection<DAGNode>>> getAllAliasModules(
			DAGPortHandler dagHandler) {
		Collection<DAGModule<Collection<DAGNode>>> aliasModules = new ArrayList<>();

		NodeAliasModule aliasModule = (NodeAliasModule) dagHandler.getDAG()
				.getModule(NodeAliasModule.class);
		if (aliasModule == null)
			throw new ModuleException(
					"Node Alias module is not in use for this DAG.");
		aliasModules.add(aliasModule);

		return aliasModules;
	}
}
