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

public class NonExistentErrorEdge extends DAGErrorEdge {
	private static final long serialVersionUID = 1L;
	private DAGNode node_;

	public NonExistentErrorEdge(DAGNode n) {
		node_ = n;
	}

	@Override
	public String getError(boolean isPretty) {
		return "The node " + node_ + " does not exist.";
	}

	@Override
	public Node[] getNodes() {
		return new Node[] { node_ };
	}

}
