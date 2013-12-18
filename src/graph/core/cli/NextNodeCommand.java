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

import graph.core.DAGNode;
import core.Command;

public class NextNodeCommand extends Command {
	@Override
	public String helpText() {
		return "{0} node : Returns the next node after 'node', in ID order.";
	}

	@Override
	public String shortDescription() {
		return "Returns the next node after the input node/ID.";
	}

	@Override
	protected void executeImpl() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		try {
			long id = -1;
			if (data.matches("\\d+"))
				id = Long.parseLong(data);
			else {
				DAGNode node = (DAGNode) dagHandler.getDAG().findOrCreateNode(
						data, null, false, false, true, false);
				if (node != null)
					id = node.getID();
			}

			if (id != -1) {
				id++;
				DAGNode nextNode = null;
				while (nextNode == null && id < DAGNode.idCounter_)
					nextNode = dagHandler.getDAG().getNodeByID(id++);
				if (nextNode == null)
					print("-1|No nodes after input node.\n");
				else
					print(nextNode.getID() + "|" + nextNode.getName() + "|"
							+ nextNode.getCreator() + "|"
							+ nextNode.getCreationDate() + "\n");
				return;
			}
		} catch (Exception e) {
		}
		print("-1|Could not parse node/ID from arguments.\n");
	}
}
