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

import java.io.BufferedReader;
import java.io.IOException;

import core.Command;

public class ShutdownCommand extends Command {

	@Override
	public String shortDescription() {
		return "Shuts down the entire DAG.";
	}

	private boolean sync_;

	@Override
	protected void executeImpl() {
		sync_ = true;
		if (!data.isEmpty() && data.equalsIgnoreCase("F"))
			sync_ = false;

		BufferedReader in = getPortHandler().getReader();
		String passphrase;
		try {
			passphrase = in.readLine();
			if (passphrase.equals("waikatoKM")) {
				print("Syncing and shutting DAG down.\n");
				new Thread(new Runnable() {
					@Override
					public void run() {
						DAGPortHandler dagHandler = (DAGPortHandler) handler;
						dagHandler.dag_.shutdown(sync_);
					}
				}).start();
				handler.terminate();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
