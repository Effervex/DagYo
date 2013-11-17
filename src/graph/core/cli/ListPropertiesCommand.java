package graph.core.cli;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.Node;

import java.util.ArrayList;
import java.util.Map;

import util.UtilityMethods;
import core.Command;

public class ListPropertiesCommand extends Command {
	@Override
	public String helpText() {
		return "{0} N/E nodeID/edgeID : Lists all properties active on the node/edge.";
	}

	@Override
	public String shortDescription() {
		return "Lists all properties active on the node/edge.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		try {
			DAGObject dagObj = null;
			if (split.get(0).equals("N")) {
				// Node
				dagObj = (DAGNode) dagHandler.getDAG().findOrCreateNode(
						split.get(1), null, false, true, false);
			} else if (split.get(0).equals("E")) {
				// Edge
				try {
					long id = Long.parseLong(split.get(1));
					dagObj = dagHandler.getDAG().getEdgeByID(id);
				} catch (Exception e) {
					Node[] edgeNodes = dagHandler.getDAG().parseNodes(
							split.get(1), null, false, false);
					dagObj = (DAGEdge) dagHandler.getDAG().findEdge(edgeNodes);
				}
			}

			if (dagObj == null) {
				print("-1|Node/Edge '" + split.get(1)
						+ "' could not be found.\n");
				return;
			}

			Map<String, String> properties = dagObj.getProperties();
			for (String key : properties.keySet()) {
				print("\"" + key + "\"=\"" + properties.get(key) + "\"\n");
			}
		} catch (Exception e) {
			print("-1|Node/Edge '" + data + "' could not be found.\n");
		}
	}
}
