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

		Long edgeID;
		try {
			edgeID = Long.parseLong(data);
		} catch (NumberFormatException e) {
			print("-1|Please enter edge ID.\n");
			return;
		}

		if (dagHandler.getDAG().removeEdge(edgeID))
			print("1|Edge successfully removed.\n");
		else
			print("-1|Could not remove edge.\n");
	}

}
