package graph.module.cli;

import graph.core.DAGNode;
import graph.core.cli.DAGPortHandler;
import graph.module.NodeAliasModule;

import java.util.ArrayList;
import java.util.Collection;

import util.UtilityMethods;
import core.Command;

/**
 * Finds a node by an alias string. This alias could be the node name or an
 * alias encoded by an edge.
 * 
 * @author Sam Sarjant
 */
public class FindNodeByAliasCommand extends Command {
	@Override
	public String helpText() {
		return "{0} alias [caseSensitive] [exactString] : "
				+ "Return all nodes with a matching alias (optionally quoted), with "
				+ "optional parameters [caseSensitive] (T/F) and "
				+ "[exactString] (T/F) to perform case-sensitive "
				+ "search or exact/prefix string search "
				+ "(defaults true for each).";
	}

	@Override
	public String shortDescription() {
		return "Finds all nodes by their name/alias.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		NodeAliasModule aliasModule = (NodeAliasModule) dagHandler.getDAG()
				.getModule(NodeAliasModule.class);
		if (aliasModule == null) {
			print("Node Alias module is not in use for this DAG.\n");
			return;
		}

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		String alias = split.get(0);
		if (alias.matches(DAGNode.QUOTED_NAME.pattern()))
			alias = UtilityMethods.shrinkString(alias, 1);
		boolean caseSensitive = true;
		if (split.size() >= 2 && split.get(1).equals("F"))
			caseSensitive = false;
		boolean exactString = true;
		if (split.size() >= 3 && split.get(2).equals("F"))
			exactString = false;
		Collection<DAGNode> nodes = aliasModule.findNodeByAlias(alias,
				caseSensitive, exactString);

		print(nodes.size() + "|");
		for (DAGNode n : nodes)
			print(dagHandler.textIDObject(n) + "|");
		print("\n");
	}
}
