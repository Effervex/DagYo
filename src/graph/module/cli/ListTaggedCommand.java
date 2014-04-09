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

import java.util.Collection;

import graph.core.DAGNode;
import graph.core.cli.CollectionCommand;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;

public class ListTaggedCommand extends CollectionCommand {

	@Override
	public String helpText() {
		return "{0} tag : Lists all nodes tagged by <tag>.";
	}

	@Override
	public String shortDescription() {
		return "Lists all tagged nodes under a given tag.";
	}

	@Override
	protected void executeImpl() {
		super.executeImpl();

		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		SubDAGExtractorModule subDAGModule = (SubDAGExtractorModule) dagHandler
				.getDAG().getModule(SubDAGExtractorModule.class);
		if (subDAGModule == null) {
			print("-1|SubDAG Extractor Module is not in use for this DAG.\n");
			return;
		}

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Collection<DAGNode> taggedNodes = subDAGModule.getTagged(data);
		if (taggedNodes == null) {
			print("0|\n");
			return;
		}
		taggedNodes = dagHandler.postProcess(taggedNodes, rangeStart_, rangeEnd_);
		
		print(taggedNodes.size() + "|");
		for (DAGNode n : taggedNodes)
			print(dagHandler.textIDObject(n) + "|");
		print("\n");
	}
}
