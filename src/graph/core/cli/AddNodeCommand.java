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
package graph.core.cli;

import graph.core.DAGNode;
import graph.core.Node;

import java.util.ArrayList;
import java.util.Arrays;

import util.UtilityMethods;
import core.Command;

public class AddNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} name [(creator)] : Adds a new named "
				+ "(or anomymous: write as \"\") node "
				+ "(quoted if name contains spaces) to the DAG "
				+ "with an optional creator argument.";
	}

	@Override
	public String shortDescription() {
		return "Creates and adds a new node to the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		Node creator = null;
		if (split.size() == 2) {
			try {
				creator = dagHandler.getDAG().findOrCreateNode(
						UtilityMethods.shrinkString(split.get(1), 1), creator);
			} catch (Exception e) {
				print("-2|Invalid creator node.\n");
				return;
			}
		}

		boolean[] flags = Arrays.copyOf(
				dagHandler.asBooleanArray(DAGPortHandler.NODE_FLAGS), 3);

		DAGNode node = null;
		if (split.get(0).equals("\"\""))
			node = (DAGNode) dagHandler.getDAG().findOrCreateNode("", creator,
					true, flags[1], true);
		else {
			node = (DAGNode) dagHandler.getDAG().findOrCreateNode(split.get(0),
					creator, true, flags[1], true);
			if (node == null) {
				print("-1|Invalid node name.\n");
				return;
			}
		}

		if (node != null)
			print(node.getID() + "|" + node.getName() + "|" + node.getCreator()
					+ "|" + node.getCreationDate() + "\n");
		else
			print("-1|Could not create node.\n");
	}
}
