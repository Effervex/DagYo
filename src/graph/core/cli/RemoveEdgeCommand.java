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

import core.Command;

public class RemoveEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} edgeID : Removes an edge from the DAG.";
	}

	@Override
	public String shortDescription() {
		return "Removes an edge from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Integer edgeID;
		try {
			edgeID = Integer.parseInt(data);
		} catch (NumberFormatException e) {
			print("-1|Please enter edge ID.\n");
			return;
		}

		dagHandler.getDAG().writeCommand("removeedge " + data);
		if (dagHandler.getDAG().removeEdge(edgeID))
			print("1|Edge successfully removed.\n");
		else
			print("-1|Could not remove edge.\n");
	}

}
