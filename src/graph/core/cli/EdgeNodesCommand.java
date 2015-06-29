package graph.core.cli;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.Node;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class EdgeNodesCommand extends DAGCommand {

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		boolean withPred = true;

		Node[] nodes = null;
		if (split.get(0).startsWith("(")) {
			// Parse edge nodes
			nodes = dagHandler.getDAG().parseNodes(split.get(0), null, false,
					false);
		} else {
			try {
				int edgeID = Integer.parseInt(split.get(0));
				DAGEdge edge = dagHandler.getDAG().getEdgeByID(edgeID);
				if (edge == null) {
					print("-1|Invalid edge ID.\n");
					return;
				}
				nodes = edge.getNodes();
			} catch (NumberFormatException e) {
				print("-1|Please enter an edge ID or edge arguments.\n");
				return;
			}
		}

		// If with pred argument
		if (split.size() >= 2 && split.get(1).equalsIgnoreCase("F"))
			withPred = false;

		// Return the nodes
		for (int i = 0; i < nodes.length; i++) {
			if (i == 0 && !withPred)
				continue;
			if (nodes[i] instanceof DAGNode)
				print(dagHandler.textIDObject(nodes[i]) + "|");
		}
		print("\n");
	}

	@Override
	public String helpText() {
		return "{0} edge [withPredicate] : Returns all non-string "
				+ "and non-primitive nodes in an edge, including the "
				+ "predicate (unless specified as false).";
	}

	@Override
	public String shortDescription() {
		return "Returns all concept nodes used in an edge.";
	}

}
