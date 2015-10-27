package graph.core.cli;

import graph.core.DAGEdge;
import graph.core.DAGObject;
import graph.core.Node;

import java.util.ArrayList;
import java.util.Collection;

import util.UtilityMethods;

public class CopyPropertiesCommand extends DAGCommand {
	@Override
	public String helpText() {
		return "() N/E source target \"sourceKey1\",\"sourceKey2\",.../ALL : "
				+ "Copies properties from the source DAG node/edge to the "
				+ "target node/edge (must eb the same). Can define which "
				+ "properties are copied, or provide the argument ALL.";
	}

	@Override
	public String shortDescription() {
		return "Copies one or more properties from the source DAG "
				+ "object to the target DAG object";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		if (split.size() != 4) {
			print("-1|Requires four arguments: node/edge, source, "
					+ "target, and keys/ALL!\n");
			return;
		}

		// Parse source/target
		DAGObject source = null;
		DAGObject target = null;
		try {
			if (split.get(0).equals("N")) {
				source = (DAGObject) dagHandler.getDAG().findOrCreateNode(
						split.get(1), null, false, false, true);
				target = (DAGObject) dagHandler.getDAG().findOrCreateNode(
						split.get(2), null, false, false, true);
			} else if (split.get(0).equals("E")) {
				try {
					int id = Integer.parseInt(split.get(1));
					source = dagHandler.getDAG().getEdgeByID(id);
				} catch (Exception e) {
					Node[] edgeNodes = dagHandler.getDAG().parseNodes(
							split.get(1), null, false, false);
					source = (DAGEdge) dagHandler.getDAG().findEdge(edgeNodes);
				}

				try {
					int id = Integer.parseInt(split.get(2));
					target = dagHandler.getDAG().getEdgeByID(id);
				} catch (Exception e) {
					Node[] edgeNodes = dagHandler.getDAG().parseNodes(
							split.get(2), null, false, false);
					target = (DAGEdge) dagHandler.getDAG().findEdge(edgeNodes);
				}
			}
		} catch (Exception e) {
			print("-1|Node/Edge '" + data + "' could not be found.\n");
		}

		// Parse key
		Collection<String> keys = new ArrayList<>();
		if (split.get(3).equals("ALL")) {
			String[] sourceProps = source.getProperties();
			for (int i = 0; i < sourceProps.length; i += 2)
				keys.add(sourceProps[i]);
		} else {
			// Split the keys
			for (String key : UtilityMethods.split(split.get(3), ','))
				keys.add(UtilityMethods.shrinkString(key, 1));
		}

		// Copy props
		for (String key : keys) {
			String value = source.getProperty(key);
			if (value != null) {
				dagHandler.getDAG().addProperty(target, key, value);
				print("\"" + key + "\"=\"" + value + "\"\n");
			}
		}
	}
}
