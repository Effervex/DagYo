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
import graph.core.Node;
import core.Command;

public class EdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} ID : Returns information about an edge by ID.";
	}

	@Override
	public String shortDescription() {
		return "Returns information about an edge by ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			int id = -1;
			DAGEdge edge = null;
			if (data.matches("\\d+")) {
				id = Integer.parseInt(data);
				edge = dagHandler.getDAG().getEdgeByID(id);
			} else {
				Node[] nodes = dagHandler.getDAG().parseNodes(data, null,
						false, false);
				if (nodes != null) {
					edge = (DAGEdge) dagHandler.getDAG().findEdge(nodes);
					id = edge.getID();
				}
			}
			if (edge != null) {
				print(id
						+ "|"
						+ edge.toString(dagHandler.get(
								DAGPortHandler.PRETTY_RESULTS).equals("false"))
						+ "|" + edge.getCreator() + "|"
						+ edge.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse edge from arguments.\n");
	}
}
