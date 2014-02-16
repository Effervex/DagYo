package graph.core.cli;

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import core.Command;

public class LastNodeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns the latest node created.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();

		long id = DAGNode.idCounter_;
		do {
			id--;
			DAGNode node = dag.getNodeByID(id);
			if (node != null) {
				print(node.getID() + "|" + node.getName() + "|"
						+ node.getCreator() + "|" + node.getCreationDate()
						+ "\n");
				return;
			}
		} while (id > 0);
		print("-1|No nodes exist.\n");
	}

}
