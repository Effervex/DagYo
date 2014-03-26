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
import graph.core.DirectedAcyclicGraph;
import core.Command;

public class LastNodeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns the latest node created.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();

		int id = DAGNode.idCounter_;
		do {
			id--;
			DAGNode node = dag.getNodeByID(id);
			if (node != null) {
				print(node.getID() + "|" + node.getName() + "|"
						+ node.getCreator() + "|" + node.getCreationDate()
						+ "\n");
				return;
			}
		} while (id > 0);
		print("-1|No nodes exist.\n");
	}

}
