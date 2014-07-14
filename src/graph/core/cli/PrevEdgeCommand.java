package graph.core.cli;

import graph.core.DAGEdge;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import core.Command;

public class PrevEdgeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} edge : Returns the previous edge before 'edge', in ID order.";
	}

	@Override
	public String shortDescription() {
		return "Returns the previous edge before the input edge/ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			int id = -1;
			if (data.matches("\\d+")) {
				id = Integer.parseInt(data);
			} else {
				Node[] nodes = dag.parseNodes(data, null, false, false);
				DAGEdge edge = (DAGEdge) dag.findEdge(nodes);
				id = edge.getID();
			}

			if (id != -1) {
				id--;
				DAGEdge nextEdge = null;
				while (nextEdge == null && id > 0)
					nextEdge = dag.getEdgeByID(id--);
				if (nextEdge == null)
					print("-1|No edges before input edge.\n");
				else
					print(nextEdge.getID()
							+ "|"
							+ nextEdge.toString(!dagHandler.get(
									DAGPortHandler.PRETTY_RESULTS).equals(
									"true")) + "|" + nextEdge.getCreator()
							+ "|" + nextEdge.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse edge/ID from arguments.\n");
	}
}
