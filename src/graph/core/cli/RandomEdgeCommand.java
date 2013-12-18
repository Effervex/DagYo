/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core.cli;

import graph.core.Identifiable;
import core.Command;

public class RandomEdgeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns a random edge from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		Identifiable obj = dagHandler.getDAG().getRandomEdge();
		if (obj == null)
			print("-1|No edges exist.\n");
		else
			print(dagHandler.textIDObject(obj) + "\n");
	}

}
