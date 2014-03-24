package graph.module.cli;

import java.io.File;

import graph.core.DirectedAcyclicGraph;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;
import core.Command;

public class ExtractSubDAGCommand extends Command {

	@Override
	public String helpText() {
		return "{0} tag folder distance : Extracts all nodes tagged by <tag> "
				+ "and any nodes up to <distance> connections from them "
				+ "and saves them into a <folder>, retaining the overall "
				+ "and relationships of the DAG.";
	}

	@Override
	public String shortDescription() {
		return "Extracts a subset of the ontology and saves it to file.";
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

		String[] split = data.split("\\s+");
		if (split.length < 3) {
			print("-1|Please enter three arguments: folder, tag, and distance.");
			return;
		}

		File folder = new File(split[1]);
		try {
			folder.mkdirs();
			int distance = Integer.parseInt(split[2]);
			boolean result = module.execute(folder, split[0], distance);
			if (result) {
				print("Extraction of SubDAG to \"" + folder + "\" successful!\n");
				return;
			}
		} catch (Exception e) {

		}
		print("Extraction of SubDAG to " + folder + " unsuccessful...\n");
	}
}
