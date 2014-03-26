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

import java.util.ArrayList;

import util.UtilityMethods;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;
import core.Command;

public class SubDAGTagCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node tag : Tags a node with a given "
				+ "tag such that when a subDAG is extracted using that "
				+ "tag, it is one of the extracted objects.";
	}

	@Override
	public String shortDescription() {
		return "Tags a node to be extracted under a specific ID.";
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

			module.tagDAGObject(node, split.get(1));
			print("1|" + dagHandler.textIDObject(node) + " tagged under \""
					+ split.get(1) + "\"\n");
		} catch (Exception e) {
			print("-1|Could not parse DAG object.\n");
		}
	}

}
