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

import core.Command;

public class NumNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} : Returns the number of nodes in the DAG.";
	}

	@Override
	public String shortDescription() {
		return "Returns the number of nodes in the DAG.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		print(dagHandler.getDAG().getNumNodes() + "\n");
	}

}
