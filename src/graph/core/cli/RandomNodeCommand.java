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
package graph.core.cli;

import java.util.Collection;
import java.util.Random;

import graph.core.DAGNode;
import graph.core.Identifiable;
import graph.module.SubDAGExtractorModule;
import core.Command;

public class RandomNodeCommand extends DAGCommand {

	@Override
	public String shortDescription() {
		return "Returns a random node from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		String filter = dagHandler.get(DAGPortHandler.SUBDAG_FILTERING);
		Identifiable obj = null;
		if (filter == null || filter.isEmpty())
			obj = dagHandler.getDAG().getRandomNode();
		else {
			SubDAGExtractorModule subDAGModule = (SubDAGExtractorModule) dagHandler
					.getDAG().getModule(SubDAGExtractorModule.class);
			Collection<DAGNode> tagged = subDAGModule.getTagged(filter);
			if (tagged != null && !tagged.isEmpty()) {
				DAGNode[] nodeArray = tagged
						.toArray(new DAGNode[tagged.size()]);
				Random rand = new Random();
				obj = nodeArray[rand.nextInt(nodeArray.length)];
			}
		}

		if (obj == null)
			print("-1|No nodes exist.\n");
		else
			print(dagHandler.textIDObject(obj) + "\n");
	}

}
