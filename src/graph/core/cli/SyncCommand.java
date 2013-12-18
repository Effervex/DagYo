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

public class SyncCommand extends Command {

	@Override
	public String shortDescription() {
		return "Synchronises the DAG information to file.";
	}

	@Override
	protected void executeImpl() {
		print("Syncing DAG information.\n");
		new Thread(new Runnable() {
			@Override
			public void run() {
				DAGPortHandler dagHandler = (DAGPortHandler) handler;
				dagHandler.dag_.saveState();
			}
		}).start();
	}

}
