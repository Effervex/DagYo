package graph.module.cli;

import graph.core.Edge;
import graph.core.Node;
import graph.core.cli.DAGPortHandler;
import graph.module.RelatedEdgeModule;

import java.util.ArrayList;
import java.util.Collection;

import util.UtilityMethods;
import core.Command;

public class RelatedEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node [(nodePosition)] {1,} : "
				+ "Returns all edges using the provided node(s), "
				+ "each optionally bounded to a specific argument "
				+ "position in the edge's arguments (1-indexed).";
	}

	@Override
	public String shortDescription() {
		return "Returns all edges that utilise the node arguments.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		RelatedEdgeModule relatedModule = (RelatedEdgeModule) dagHandler
				.getDAG().getModule(RelatedEdgeModule.class);
		if (relatedModule == null) {
			print("Related Edge module is not in use for this DAG.\n");
			return;
		}

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Object[] args = null;
		try {
			args = parseArgs(data, dagHandler);
			if (args == null)
				return;
		} catch (Exception e) {
			print("-1|Could not parse arguments.\n");
			return;
		}

		Collection<Edge> edges = relatedModule.execute(args);
		print(edges.size() + "|");
		for (Edge edge : edges) {
			print(dagHandler.textIDObject(edge) + "|");
		}
		print("\n");
	}

	protected Object[] parseArgs(String data, DAGPortHandler dagHandler) {
		ArrayList<Object> args = new ArrayList<>();
		int i = 0;
		ArrayList<String> split = UtilityMethods.split(data, ' ');
		while (i < split.size()) {
			Node node = dagHandler.getDAG().findOrCreateNode(split.get(i++),
					null, false, false, false);
			if (node == null) {
				print("-1|No node found.\n");
				return null;
			}
			args.add(node);

			if (i < split.size() && split.get(i).matches("\\(-?\\d+\\)")) {
				String indexStr = split.get(i++);
				
				int argPos = Integer.parseInt(UtilityMethods.shrinkString(
						indexStr, 1));
				args.add(argPos);
			}
		}
		return args.toArray(new Object[args.size()]);
	}
}
