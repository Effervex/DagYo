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

import util.BooleanFlags;
import graph.core.DirectedAcyclicGraph;
import commands.VarHelpCommand;

public class DAGVarHelpCommand extends VarHelpCommand {
	@Override
	protected String getVarHelp(String variable) {
		if (variable.equals(DAGPortHandler.DYNAMICALLY_ADD_NODES))
			return "[BOOL] If new edges also create new nodes "
					+ "if the edge's nodes do not exist.";
		if (variable.equals(DAGPortHandler.PRETTY_RESULTS))
			return "[BOOL+\"only\"] Modifies the command outputs "
					+ "of edges and nodes to either ID, ID:toString, "
					+ "or toString for 'false', 'true', and 'only' "
					+ "respectively.";
		if (variable.equals(DAGPortHandler.SORT_ORDER))
			return "[STRING] Enforces a sort order on certain command "
					+ "outputs (using DAGPortHandler's 'sort'). The "
					+ "different sort orders include: alpha, for "
					+ "alphabetical; alphaNoCase, for alphabetical "
					+ "(caseless); id, for ID ordered results; "
					+ "alias, for sort by alias; "
					+ "numedge, for sort by descending number of edges.";
		if (variable.equals(DAGPortHandler.EDGE_FLAGS)
				|| variable.equals(DAGPortHandler.NODE_FLAGS)) {
			String dagObj = "edge";
			BooleanFlags bFlags = DirectedAcyclicGraph.edgeFlags_;
			if (variable.equals(DAGPortHandler.NODE_FLAGS)) {
				dagObj = "nodes";
				bFlags = DirectedAcyclicGraph.nodeFlags_;
			}
			return "[STRING] Applies the set flags during " + dagObj
					+ " creation (using 'T/F' for true/false). The "
					+ "flags represent (in order): " + bFlags.flagString();
		}
		return super.getVarHelp(variable);
	}
}
