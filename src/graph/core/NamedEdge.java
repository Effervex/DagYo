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

import java.io.IOException;
import java.io.ObjectInput;

public class NamedEdge extends DAGEdge {
	private static final long serialVersionUID = 6165829860550881561L;
	protected DAGNode edgeName_;

	public NamedEdge(DAGNode edgeName, Node... nodes) {
		super(prefixName(edgeName, nodes));
		edgeName_ = edgeName;
	}

	private static Node[] prefixName(DAGNode edgeName, Node[] nodes) {
		Node[] prefixed = new Node[nodes.length + 1];
		prefixed[0] = edgeName;
		System.arraycopy(nodes, 0, prefixed, 1, nodes.length);
		return prefixed;
	}

	public DAGNode getEdgeName() {
		return edgeName_;
	}

	@Override
	protected void readFullObject(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readFullObject(in);
		edgeName_ = (DAGNode) edgeNodes_[0];
	}
}
