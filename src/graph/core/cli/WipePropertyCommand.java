package graph.core.cli;

import gnu.trove.iterator.TIntObjectIterator;
import graph.core.DAGObject;

import java.util.ArrayList;
import java.util.Collection;

import util.UtilityMethods;
import util.collection.trove.TIndexedCollection;
import core.Command;

public class WipePropertyCommand extends Command {
	@Override
	public String helpText() {
		return "{0} N/E property : Removes property from every node or edge.";
	}

	@Override
	public String shortDescription() {
		return "Removes a property from all nodes/edges.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		if (split.size() < 2) {
			print("-1|Please enter two arguments: N/E and property.\n");
			return;
		}

		TIndexedCollection<? extends DAGObject> dagObjs = null;
		if (split.get(0).equals("N"))
			dagObjs = dagHandler.getDAG().getNodes();
		else if (split.get(0).equals("E"))
			dagObjs = dagHandler.getDAG().getEdges();
		else {
			print("-1|Specify either node N or edge E.\n");
			return;
		}

		// For every DAG object
		String property = split.get(1);
		TIntObjectIterator<? extends DAGObject> iter = dagObjs.iterator();
		for (int i = dagObjs.size(); i-- > 0;) {
			iter.advance();
			dagHandler.getDAG().removeProperty(iter.value(), property);
		}
		print(property + " successfully wiped.\n");
	}

}
