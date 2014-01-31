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
import core.CommandParser;

public class CountCommand extends Command {
	@Override
	public String helpText() {
		return "{1} <command + args> : "
				+ "Returns only the count (first argument) of the output of the command.";
	}

	@Override
	public String shortDescription() {
		return "Returns the count of another command's output.";
	}

	@Override
	protected void executeImpl() {
		// Format each element into individual commands
		if (data.length() == 0) {
			print("-1|No command or delimiter specified.\n");
			return;
		}

		try {
			Command command = CommandParser.parse(data);
			command.setPortHandler(handler);
			command.execute();
			String result = command.getResult();
			int index = result.indexOf("|");
			if (index != -1)
				print(result.substring(0, index) + "\n");
			else
				print("-1|No count value found.\n");
		} catch (Exception e) {
			print("-1|Invalid command.\n");
		}
	}

}
