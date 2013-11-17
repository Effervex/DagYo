package graph.core.cli;

import graph.core.DAGEdge;
import graph.core.Edge;
import graph.core.ErrorEdge;
import graph.core.Node;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class AddEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} (node node ...) [(creator)] : Creates an edge "
				+ "composed of two or more nodes and an optional creator.";
	}

	@Override
	public String shortDescription() {
		return "Creates and adds a new edge to the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		Node creator = null;
		if (split.size() == 2) {
			try {
				creator = dagHandler.getDAG().findOrCreateNode(
						UtilityMethods.shrinkString(split.get(1), 1), creator,
						false, false, false);
			} catch (Exception e) {
				print("-1|Invalid creator node.\n");
				return;
			}
		}

		try {
			Node[] nodes = dagHandler.getDAG().parseNodes(
					split.get(0),
					creator,
					dagHandler.get(DAGPortHandler.DYNAMICALLY_ADD_NODES)
							.equals("true"), false);
			if (nodes == null) {
				print("-1|Problem parsing nodes.\n");
				return;
			}
			Edge edge = dagHandler.getDAG().findOrCreateEdge(creator, false,
					nodes);

			if (edge instanceof ErrorEdge) {
				print("-1|" + ((ErrorEdge) edge).getError() + "\n");
			} else {
				DAGEdge dagEdge = (DAGEdge) edge;
				print(dagEdge.getID()
						+ "|"
						+ edge.toString(!dagHandler.get(
								DAGPortHandler.PRETTY_RESULTS).equals("true"))
						+ "|" + dagEdge.getCreator() + "|"
						+ dagEdge.getCreationDate() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			print("-1|Problem parsing nodes.\n");
			return;
		}
	}
}
