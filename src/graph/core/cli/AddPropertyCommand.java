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

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Command;

public class AddPropertyCommand extends Command {
	private static final Pattern ARG_PATTERN = Pattern
			.compile("^([NE])\\s(.+?)\\s\"([^\"]+?)\"\\s(\\S+?)$");

	@Override
	public String helpText() {
		return "{0} N/E nodeID/edgeID \"key\" delimiter : "
				+ "Reads text until the delimiter is read on its own line, "
				+ "at which point the text is assigned to the key for the "
				+ "DAG object.";
	}

	@Override
	public String shortDescription() {
		return "Adds a named property to a node/edge using delimiters.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		DirectedAcyclicGraph dag = dagHandler.getDAG();
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Matcher m = ARG_PATTERN.matcher(data);
		if (m.matches()) {
			DAGObject dagObj = null;
			if (m.group(1).equals("N")) {
				// Node
				dagObj = (DAGNode) dag.findOrCreateNode(m.group(2), null,
						false, false, true);
			} else if (m.group(1).equals("E")) {
				// Edge
				try {
					long id = Long.parseLong(m.group(2));
					dagObj = dag.getEdgeByID(id);
				} catch (Exception e) {
					Node[] edgeNodes = dag.parseNodes(m.group(2), null, false,
							false);
					dagObj = (DAGEdge) dag.findEdge(edgeNodes);
				}
			}

			if (dagObj == null) {
				print("-1|Node/Edge '" + m.group(2) + "' could not be found.\n");
				return;
			}

			String key = m.group(3);
			String delimiter = m.group(4);

			try {
				BufferedReader in = dagHandler.getReader();
				String line = null;
				StringBuffer valueBuf = new StringBuffer();
				while ((line = in.readLine()) != null
						&& !line.equals(delimiter)) {
					if (valueBuf.length() != 0)
						valueBuf.append("\n");
					valueBuf.append(line);
				}

				String value = valueBuf.toString();
				dag.addProperty(dagObj, key, value);
				dagHandler.getDAG().writeCommand(
						"addprop " + data + "\\n"
								+ value.replaceAll("\\n", "\\\\n") + "\\n"
								+ delimiter);
				print(dagHandler.textIDObject(dagObj) + ": \"" + key + "\"=\""
						+ value + "\"\n");
			} catch (Exception e) {
				print("-1|Error parsing properties.\n");
			}
		} else {
			print("-1|Could not parse arguments.\n");
		}
	}
}
