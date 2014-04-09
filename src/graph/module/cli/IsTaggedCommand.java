package graph.module.cli;

import java.util.ArrayList;

import util.UtilityMethods;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;
import core.Command;

public class IsTaggedCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node tag : Checks if node is tagged under tag.";
	}

	@Override
	public String shortDescription() {
		return "Checks if node is tagged under tag.";
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
		if (split.size() < 2) {
			print("-1|Please enter 2 arguments: node and tag.\n");
			return;
		}
		
		try {
			DAGNode node = (DAGNode) dag.findOrCreateNode(split.get(0), null,
					false);

			if (module.isTagged(node, split.get(1)))
				print("1|T\n");
			else
				print("0|F\n");
		} catch (Exception e) {
			print("-1|Could not parse DAG object.\n");
		}
	}

}
