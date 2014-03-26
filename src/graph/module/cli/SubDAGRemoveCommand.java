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

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;

import java.util.ArrayList;

import util.UtilityMethods;
import core.Command;

public class SubDAGRemoveCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node tag : Removes a tag from a node such "
				+ "that it is no longer extracted.";
	}

	@Override
	public String shortDescription() {
		return "Untags a node to be extracted under a specific ID.";
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

			module.removeTagDAGObject(node, split.get(1));
			print("1|" + dagHandler.textIDObject(node) + " untagged.\n");
		} catch (Exception e) {
			print("-1|Could not parse DAG object.\n");
		}
	}

}
