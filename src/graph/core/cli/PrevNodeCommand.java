package graph.core.cli;

import graph.core.DAGNode;
import core.Command;

public class PrevNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node : Returns the previous node before 'node', in ID order.";
	}

	@Override
	public String shortDescription() {
		return "Returns the previous node before the input node/ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			int id = -1;
			if (data.matches("\\d+"))
				id = Integer.parseInt(data);
			else {
				DAGNode node = (DAGNode) dagHandler.getDAG().findOrCreateNode(
						data, null, false, false, true);
				if (node != null)
					id = node.getID();
			}

			if (id != -1) {
				id--;
				DAGNode nextNode = null;
				while (nextNode == null && id > 0)
					nextNode = dagHandler.getDAG().getNodeByID(id--);
				if (nextNode == null)
					print("-1|No nodes before input node.\n");
				else
					print(nextNode.getID() + "|" + nextNode.getName() + "|"
							+ nextNode.getCreator() + "|"
							+ nextNode.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse node/ID from arguments.\n");
	}
}
