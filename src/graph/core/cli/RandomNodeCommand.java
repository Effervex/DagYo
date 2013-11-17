package graph.core.cli;

import graph.core.Identifiable;
import core.Command;

public class RandomNodeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns a random node from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		Identifiable obj = dagHandler.getDAG().getRandomNode();
		if (obj == null)
			print("-1|No nodes exist.\n");
		else
			print(dagHandler.textIDObject(obj) + "\n");
	}

}
