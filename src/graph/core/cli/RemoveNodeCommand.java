package graph.core.cli;

import graph.core.DAGNode;
import core.Command;

public class RemoveNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node : Removes a specific node from "
				+ "the DAG. All edges including the node are "
				+ "also automatically removed.";
	}

	@Override
	public String shortDescription() {
		return "Removes a specific node from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			DAGNode node = (DAGNode) dagHandler.getDAG().findOrCreateNode(data,
					null, false, true, false);
			if (node != null && dagHandler.getDAG().removeNode(node.getID()))
				print("1|Node successfully removed.\n");
			else
				print("-1|Could not remove node.\n");
		} catch (Exception e) {
			print("-1|Node '" + data + "' could not be found.\n");
		}
	}

}
