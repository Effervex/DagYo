package graph.core.cli;

import graph.core.DAGNode;
import graph.core.Node;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class AddNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} name [(creator)] : Adds a new named "
				+ "(or anomymous: write as \"\") node "
				+ "(quoted if name contains spaces) to the DAG "
				+ "with an optional creator argument.";
	}

	@Override
	public String shortDescription() {
		return "Creates and adds a new node to the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		Node creator = null;
		if (split.size() == 2) {
			try {
				creator = dagHandler.getDAG().findOrCreateNode(
						UtilityMethods.shrinkString(split.get(1), 1), creator,
						false, false, false);
			} catch (Exception e) {
				print("-2|Invalid creator node.\n");
				return;
			}
		}

		DAGNode node = null;
		if (split.get(0).equals("\"\""))
			node = (DAGNode) dagHandler.getDAG().findOrCreateNode("", creator,
					true, true, true);
		else {
			node = (DAGNode) dagHandler.getDAG().findOrCreateNode(split.get(0),
					creator, true, true, true);
			if (node == null) {
				print("-1|Invalid node name.\n");
				return;
			}
		}

		if (node != null)
			print(node.getID() + "|" + node.getName() + "|" + node.getCreator()
					+ "|" + node.getCreationDate() + "\n");
		else
			print("-1|Could not create node.\n");
	}
}
