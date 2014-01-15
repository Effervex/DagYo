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
import graph.module.ModuleException;
import graph.module.NodeAliasModule;

import java.util.ArrayList;
import java.util.Collection;
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

		Collection<AliasModule> aliasModules = null;
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
		boolean aliasText = false;
		if (split.size() >= 4 && split.get(3).equals("T"))
			aliasText = true;

		if (!aliasText) {
			Collection<DAGNode> nodes = new TreeSet<>();
			for (AliasModule aliasModule : aliasModules) {
				nodes.addAll(aliasModule.findNodes(alias, caseSensitive,
						exactString));
			}
			nodes = dagHandler.sort(nodes, rangeStart_, rangeEnd_);

			print(nodes.size() + "|");
			for (DAGNode n : nodes)
				print(dagHandler.textIDObject(n) + "|");
		} else {
			Collection<AliasedObject<Character, DAGNode>> nodes = new TreeSet<>();
			for (AliasModule aliasModule : aliasModules) {
				nodes.addAll(aliasModule.findAliasedNodes(alias, caseSensitive,
						exactString));
			}
			nodes = dagHandler.sort(nodes, rangeStart_, rangeEnd_);

			print(nodes.size() + "|");
			for (AliasedObject<Character, DAGNode> n : nodes)
				print(dagHandler.textIDObject(n.object_) + ",\""
						+ new String(ArrayUtils.toPrimitive(n.alias_)) + "\"|");
		}
		print("\n");
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
