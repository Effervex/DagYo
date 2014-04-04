/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sam Sarjant - initial API and implementation
 ******************************************************************************/
package graph.module.cli;

import graph.core.Edge;
import graph.core.Node;
import graph.core.cli.CollectionCommand;
import graph.core.cli.DAGPortHandler;
import graph.module.RelatedEdgeModule;

import java.util.ArrayList;
import java.util.Collection;

import util.UtilityMethods;

public class RelatedEdgeCommand extends CollectionCommand {
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
		super.executeImpl();
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		RelatedEdgeModule relatedModule = (RelatedEdgeModule) dagHandler
				.getDAG().getModule(RelatedEdgeModule.class);
		if (relatedModule == null) {
			print("-1|Related Edge module is not in use for this DAG.\n");
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
		edges = dagHandler.postProcess(edges, rangeStart_, rangeEnd_);

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
					null);
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
