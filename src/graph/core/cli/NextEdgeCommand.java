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
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import core.Command;

public class NextEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} edge : Returns the next edge after 'edge', in ID order.";
	}

	@Override
	public String shortDescription() {
		return "Returns the next edge after the input edge/ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			int id = -1;
			if (data.matches("\\d+")) {
				id = Integer.parseInt(data);
			} else {
				Node[] nodes = dag.parseNodes(data, null, false, false);
				DAGEdge edge = (DAGEdge) dag.findEdge(nodes);
				id = edge.getID();
			}

			if (id != -1) {
				id++;
				DAGEdge nextEdge = null;
				while (nextEdge == null && id < DAGEdge.idCounter_)
					nextEdge = dag.getEdgeByID(id++);
				if (nextEdge == null)
					print("-1|No edges after input edge.\n");
				else
					print(nextEdge.getID()
							+ "|"
							+ nextEdge.toString(!dagHandler.get(
									DAGPortHandler.PRETTY_RESULTS).equals(
									"true")) + "|" + nextEdge.getCreator()
							+ "|" + nextEdge.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse edge/ID from arguments.\n");
	}
}
