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

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.Node;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class RemovePropertyCommand extends Command {
	@Override
	public String helpText() {
		return "{0} N/E nodeID/edgeID propertyKey : "
				+ "Removes a property from the node/edge.";
	}

	@Override
	public String shortDescription() {
		return "Removes a property from the node/edge.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		try {
			DAGObject dagObj = null;
			if (split.get(0).equals("N")) {
				// Node
				dagObj = (DAGNode) dagHandler.getDAG().findOrCreateNode(
						split.get(1), null, false, false, true, false);
			} else if (split.get(0).equals("E")) {
				// Edge
				try {
					long id = Long.parseLong(split.get(1));
					dagObj = dagHandler.getDAG().getEdgeByID(id);
				} catch (Exception e) {
					Node[] edgeNodes = dagHandler.getDAG().parseNodes(
							split.get(1), null, false, false);
					dagObj = (DAGEdge) dagHandler.getDAG().findEdge(edgeNodes);
				}
			}

			if (dagObj == null) {
				print("-1|Node/Edge '" + split.get(1)
						+ "' could not be found.\n");
				return;
			}

			String key = split.get(2);
			if (key.matches(DAGNode.QUOTED_NAME.pattern()))
				key = UtilityMethods.shrinkString(key, 1);

			dagHandler.getDAG().removeProperty(dagObj, key);
			print("\"" + key + "\" removed from "
					+ dagHandler.textIDObject(dagObj) + "\n");
		} catch (Exception e) {
			print("-1|Node/Edge '" + split.get(1) + "' could not be found.\n");
		}
	}
}
