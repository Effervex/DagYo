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
import core.Command;

public class LastEdgeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns the latest edge created.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();

		int id = DAGEdge.idCounter_;
		do {
			id--;
			DAGEdge edge = dag.getEdgeByID(id);
			if (edge != null) {
				print(id
						+ "|"
						+ edge.toString(!dagHandler.get(
								DAGPortHandler.PRETTY_RESULTS).equals("true"))
						+ "|" + edge.getCreator() + "|"
						+ edge.getCreationDate() + "\n");
				return;
			}
		} while (id > 0);
		print("-1|No edges exist.\n");
	}
}
