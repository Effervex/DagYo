/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core.cli;

import core.Command;

public class GroundEphemeralCommand extends Command {
	@Override
	public String shortDescription() {
		return "Grounds ephemeral nodes and edges, then rebuilds and saves modules.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		dagHandler.getDAG().groundEphemeral();
	}

}
