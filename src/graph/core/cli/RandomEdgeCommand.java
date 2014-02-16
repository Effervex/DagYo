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

import graph.core.Identifiable;
import core.Command;

public class RandomEdgeCommand extends Command {

	@Override
	public String shortDescription() {
		return "Returns a random edge from the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		Identifiable obj = dagHandler.getDAG().getRandomEdge();
		if (obj == null)
			print("-1|No edges exist.\n");
		else
			print(dagHandler.textIDObject(obj) + "\n");
	}

}
