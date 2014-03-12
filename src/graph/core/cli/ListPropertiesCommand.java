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

public class ListPropertiesCommand extends Command {
	@Override
	public String helpText() {
		return "{0} N/E nodeID/edgeID : Lists all properties active on the node/edge.";
	}

	@Override
	public String shortDescription() {
		return "Lists all properties active on the node/edge.";
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
						split.get(1), null, false, false, true);
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

			String[] properties = dagObj.getProperties();
			for (int i = 0; i < properties.length; i += 2) {
				print("\"" + properties[i] + "\"=\"" + properties[i + 1]
						+ "\"\n");
			}
		} catch (Exception e) {
			print("-1|Node/Edge '" + data + "' could not be found.\n");
		}
	}
}
