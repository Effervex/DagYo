/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sam Sarjant - initial API and implementation
 ******************************************************************************/
package graph.module.cli;

import graph.core.DirectedAcyclicGraph;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;

import core.Command;

public class GrowSubDAGCommand extends Command {
	@Override
	public String helpText() {
		return "{0} tag distance : Expands the tags of a subDAG by "
				+ "a given distance. A distance of 0 may have effects "
				+ "as default expansion effects take place.";
	}

	@Override
	public String shortDescription() {
		return "Expands the tags of a subDAG by a given distance.";
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
		if (split.length < 2) {
			print("-1|Please enter two arguments: tag and distance.\n");
			return;
		}

		try {
			int distance = Integer.parseInt(split[1]);
			boolean result = module.growSubDAG(split[0], distance);
			if (result) {
				print("Growth of SubDAG successful!\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		print("Growth of SubDAG unsuccessful...\n");
	}

}
