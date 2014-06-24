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
package graph.core.cli;

import java.io.File;

import graph.core.DAGExportFormat;
import core.Command;

public class ExportCommand extends Command {
	@Override
	public String helpText() {
		StringBuilder formats = new StringBuilder();
		for (DAGExportFormat format : DAGExportFormat.values())
			formats.append(" " + format.toString());
		return "{0} file format : Exports the DAG to text file such that "
				+ "it can be rebuilt from that text file. The format "
				+ "determines what format the export is in. Available "
				+ "formats include:" + formats.toString();
	}

	@Override
	public String shortDescription() {
		return "Exports this DAG to file.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		String[] split = data.split("\\s");
		if (split.length != 2) {
			print("-1|Please enter two arguments: file and a valid format.\n");
			return;
		}

		try {
			File file = new File(split[0]);
			file.createNewFile();

			DAGExportFormat format = DAGExportFormat.valueOf(split[1]);

			dagHandler.getDAG().export(file, format);
			print("DAG exported to " + file + " successfully!\n");
		} catch (Exception e) {
			print("-13|Exception during export: " + e + "\n");
			return;
		}
	}

}
