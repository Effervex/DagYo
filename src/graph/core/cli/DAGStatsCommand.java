package graph.core.cli;

import graph.module.DAGModule;

import java.util.Map;
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
		Map<String, DAGModule<?>> modules = dagHandler.getDAG().getModules();
		for (String modName : modules.keySet()) {
			print("\t" + modName + ": " + modules.get(modName) + "\n");
		}
	}
}
