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
import java.io.ObjectOutput;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * An unnamed edge between two or more nodes. The nodes are not necessarily
 * ordered - it is up to the agent to determine if they are or not.
 * 
 * @author Sam Sarjant
 */
public class DAGEdge extends DAGObject implements Edge {
	private static final long serialVersionUID = 8148137157637389069L;

	/** The counter for assigning ids to edges. */
	public static int idCounter_ = 1;

	/** The nodes of the edge. */
	protected Node[] edgeNodes_;

	public DAGEdge() {
		super();
	}

	/**
	 * An edge involving two or more nodes.
	 * 
	 * @param nodes
	 *            The nodes of the edge.
	 */
	public DAGEdge(Node... nodes) {
		this(null, true, nodes);
	}

	/**
	 * An edge involving two or more nodes created by a creator.
	 * 
	 * @param creator
	 *            The creator of the node.
	 * @param placeholder
	 *            A throwaway delimiter. Only to distinguish arguments.
	 * @param nodes
	 *            The nodes of the edge.
	 */
	public DAGEdge(Node creator, boolean placeholder, Node... nodes) {
		super(creator);
		if (nodes.length < 2)
			throw new IllegalArgumentException(
					"An edge must be between two or more nodes. "
							+ Arrays.toString(nodes));
		edgeNodes_ = new Node[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == null)
				throw new IllegalArgumentException(
						"Cannot initialise edge with null arguments! "
								+ Arrays.toString(nodes));
			edgeNodes_[i] = nodes[i];
		}
	}

	@Override
	protected void readFullObject(ObjectInput in) throws IOException,
			ClassNotFoundException {
		edgeNodes_ = (Node[]) in.readObject();
	}

	@Override
	protected int requestID() {
		return idCounter_++;
	}

	@Override
	protected void writeFullObject(ObjectOutput out) throws IOException {
		out.writeObject(edgeNodes_);
	}

	@Override
	public boolean containsNode(Node node) {
		for (Node edgeNode : getNodes())
			if (edgeNode.equals(node))
				return true;
		return false;
	}

	@Override
	public Node[] getNodes() {
		return edgeNodes_;
	}

	@Override
	public String toString() {
		return "(" + StringUtils.join(edgeNodes_, ' ') + ")";
	}

	@Override
	public String toString(boolean useIDs) {
		if (!useIDs)
			return toString();
		StringBuffer buffer = new StringBuffer("(");
		boolean first = true;
		for (Node n : edgeNodes_) {
			if (!first)
				buffer.append(" ");
			buffer.append(n.getIdentifier());
			first = false;
		}
		buffer.append(")");
		return buffer.toString();
	}

	public static void setCounter(int count) {
		idCounter_ = count;
	}
}
