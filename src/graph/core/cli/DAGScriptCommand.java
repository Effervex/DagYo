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

import commands.ScriptCommand;

public class DAGScriptCommand extends ScriptCommand {
	@Override
	public void execute() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		// TODO Change this so the flags are set to ephemeral instead.
		dagHandler.getDAG().noChecks_ = true;
		super.execute();
		dagHandler.getDAG().noChecks_ = false;
		dagHandler.getDAG().reloadModules(false);
	}
}
