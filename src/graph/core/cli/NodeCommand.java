package graph.core.cli;

import graph.core.DAGNode;
import core.Command;

public class NodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} ID : Returns information about a node by ID.";
	}

	@Override
	public String shortDescription() {
		return "Returns information about a node by ID.";
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
			if (node != null) {
				print(node.getID() + "|" + node.getName() + "|"
						+ node.getCreator() + "|"
						+ node.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse node from arguments.\n");
	}

}
