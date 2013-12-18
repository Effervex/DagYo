/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core.cli;

import graph.module.DAGModule;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import core.Command;

public class DAGStatsCommand extends Command {

	@Override
	public String shortDescription() {
		return "Outputs a brief overview of various DAG statistics.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		long elapsed = System.currentTimeMillis()
				- dagHandler.getDAG().startTime_;
		String elapsedStr = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(elapsed),
				TimeUnit.MILLISECONDS.toMinutes(elapsed)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(elapsed)),
				TimeUnit.MILLISECONDS.toSeconds(elapsed)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(elapsed)));
		print("Uptime: " + elapsedStr + "\n");
		print("Num nodes: " + dagHandler.getDAG().getNumNodes() + "\n");
		print("Num edges: " + dagHandler.getDAG().getNumEdges() + "\n");
		print("Active modules:\n");
		Collection<DAGModule<?>> modules = dagHandler.getDAG().getModules();
		for (DAGModule<?> modName : modules)
			print("\t" + modName + "\n");
	}
}
