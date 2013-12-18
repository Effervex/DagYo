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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.UtilityMethods;
import core.Command;
import core.CommandParser;

public class MapCommand extends Command {

	@Override
	public String helpText() {
		return "{0} function funcArgs delimiter \\n collection collectionArgs "
				+ "\\n captureRegExp : Applies function with "
				+ "args (using $1, $2,... syntax for regexp) to every "
				+ "item in the output of collection using collectionArgs. "
				+ "Every output is separated by delimiter. Note that "
				+ "multiline commands may not be usable in this command.";
	}

	@Override
	public String shortDescription() {
		return "Uses the outputs of one command as input to another.";
	}

	@Override
	protected void executeImpl() {
		// Format each element into individual commands
		int spaceIndex = data.indexOf(' ');
		if (spaceIndex == -1) {
			print("No function or args specified.\n");
			return;
		}
		int lastSpace = data.lastIndexOf(' ');
		if (lastSpace == spaceIndex) {
			print("No args or delimiter specified.\n");
			return;
		}

		BufferedReader in = getPortHandler().getReader();
		String functionName = data.substring(0, spaceIndex);
		String funcArgs = data.substring(spaceIndex + 1, lastSpace).trim();
		String delimiter = data.substring(lastSpace).trim();

		try {
			// Read collection
			String collectionCommand = in.readLine().trim();
			spaceIndex = collectionCommand.indexOf(' ');
			String collectionName = collectionCommand;
			String collectionArgs = "";
			if (spaceIndex != -1) {
				collectionName = collectionCommand.substring(0, spaceIndex);
				collectionArgs = collectionCommand.substring(spaceIndex + 1)
						.trim();
			}

			// Run the command
			Command command = CommandParser.parse(collectionName + " "
					+ collectionArgs);
			command.setPortHandler(handler);
			command.execute();
			String output = command.getResult();

			// Read regex
			String regexStr = in.readLine().trim();
			Pattern regex = Pattern.compile(regexStr);

			// Map function to it
			Matcher m = regex.matcher(output);
			while (m.find()) {
				String[] groups = new String[m.groupCount() + 1];
				for (int i = 0; i < groups.length; i++)
					groups[i] = m.group(i);

				Command funcCommand = CommandParser.parse(functionName + " "
						+ UtilityMethods.replaceToken(funcArgs, groups));
				funcCommand.setPortHandler(handler);
				funcCommand.execute();
				print(funcCommand.getResult());
				print(delimiter + "\n");
			}
		} catch (Exception e) {
			print("" + e);
		}
	}
}
