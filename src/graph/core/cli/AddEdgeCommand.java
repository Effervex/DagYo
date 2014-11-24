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
import graph.core.Edge;
import graph.core.ErrorEdge;
import graph.core.Node;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class AddEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} (node node ...) [(creator)] : Creates an edge "
				+ "composed of two or more nodes and an optional creator.";
	}

	@Override
	public String shortDescription() {
		return "Creates and adds a new edge to the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		Node creator = null;
		if (split.size() == 2) {
			try {
				creator = dagHandler.getDAG().findOrCreateNode(
						UtilityMethods.shrinkString(split.get(1), 1), creator);
			} catch (Exception e) {
				print("-1|Invalid creator node.\n");
				return;
			}
		}

		try {
			Node[] nodes = dagHandler.getDAG().parseNodes(
					split.get(0),
					creator,
					dagHandler.get(DAGPortHandler.DYNAMICALLY_ADD_NODES)
							.equals("true"), false);
			if (nodes == null) {
				print("-1|Problem parsing nodes.\n");
				return;
			}
			boolean[] flags = dagHandler
					.asBooleanArray(DAGPortHandler.EDGE_FLAGS);
			if (flags.length < 1)
				flags = new boolean[1];
			flags[0] = true;
			Edge edge = dagHandler.getDAG().findOrCreateEdge(nodes, creator,
					flags);
			dagHandler.getDAG().writeCommand("addedge " + data);

			if (edge instanceof ErrorEdge) {
				print("-1|" + ((ErrorEdge) edge).getError(true) + "\n");
			} else {
				DAGEdge dagEdge = (DAGEdge) edge;
				print(dagEdge.getID()
						+ "|"
						+ edge.toString(!dagHandler.get(
								DAGPortHandler.PRETTY_RESULTS).equals("true"))
						+ "|" + dagEdge.getCreator() + "|"
						+ dagEdge.getCreationDate() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			print("-1|Problem parsing nodes.\n");
			return;
		}
	}
}
