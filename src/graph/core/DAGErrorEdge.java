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
package graph.core;


public class DAGErrorEdge implements ErrorEdge {
	private static final long serialVersionUID = 1L;
	private Node[] nodes_;

	public DAGErrorEdge() {
		nodes_ = null;
	}

	public DAGErrorEdge(Node[] nodes) {
		nodes_ = nodes;
	}

	@Override
	public final boolean containsNode(Node node) {
		return false;
	}

	@Override
	public final String toString(boolean useIDs) {
		return getError(!useIDs);
	}

	public final String toString() {
		return getError(false);
	}

	@Override
	public final String getIdentifier() {
		return getError(false);
	}

	@Override
	public final String getIdentifier(boolean useName) {
		return getError(useName);
	}

	@Override
	public final int getID() {
		return -1;
	}

	@Override
	public Node[] getNodes() {
		return nodes_;
	}

	@Override
	public String getError(boolean isPretty) {
		StringBuilder builder = new StringBuilder("Edge '");
		boolean useSpace = false;
		for (Node n : nodes_) {
			if (useSpace)
				builder.append(" ");
			builder.append(n.getIdentifier(isPretty));
			useSpace = true;
		}
		return "Edge '" + builder.toString() + "' is invalid.";
	}
}
