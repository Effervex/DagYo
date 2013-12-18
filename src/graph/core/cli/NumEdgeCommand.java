/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core.cli;

import core.Command;

public class NumEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} : Returns the number of edges in the DAG.";
	}

	@Override
	public String shortDescription() {
		return "Returns the number of edges in the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		print(dagHandler.getDAG().getNumEdges() + "\n");
	}

}
