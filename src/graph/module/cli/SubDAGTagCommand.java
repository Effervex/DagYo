package graph.module.cli;

import java.util.ArrayList;

import util.UtilityMethods;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;
import core.Command;

public class SubDAGTagCommand extends Command {
	@Override
	public String helpText() {
		return "{0} N/E nodeID/edgeID tag : Tags node/edge with a given "
				+ "tag such that when a subDAG is extracted using that "
				+ "tag, it is one of the extracted objects.";
	}

	@Override
	public String shortDescription() {
		return "Tags a node or edge to be extracted under a specific ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();
		SubDAGExtractorModule module = (SubDAGExtractorModule) dag
				.getModule(SubDAGExtractorModule.class);
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}
		if (module == null) {
			print("-1|SubDAG Extractor module is not in use for this DAG.\n");
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		if (split.size() < 3) {
			print("-1|Please enter 3 arguments: type, id, and tag.\n");
			return;
		}

		DAGObject dagObj = null;
		try {
			if (split.get(0).equals("N"))
				dagObj = (DAGObject) dag.findOrCreateNode(split.get(1), null,
						false);
			if (split.get(0).equals("E")) {
				Node[] parse = dag.parseNodes(split.get(1), null, false, false);
				dagObj = (DAGObject) dag.findOrCreateEdge(parse, null, false);
			}

			module.tagDAGObject(dagObj, split.get(2));
			print(dagHandler.textIDObject(dagObj) + " tagged under \""
					+ split.get(2) + "\"\n");
		} catch (Exception e) {
			print("-1|Could not parse DAG object.\n");
		}
	}

}
