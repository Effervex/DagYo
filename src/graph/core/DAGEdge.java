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
	/** The counter for assigning ids to edges. */
	public static long idCounter_ = 0;

	private static final long serialVersionUID = 8148137157637389069L;

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
		id_ = idCounter_++;
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
	public boolean containsNode(Node node) {
		for (Node edgeNode : getNodes())
			if (edgeNode.equals(node))
				return true;
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DAGEdge other = (DAGEdge) obj;
		if (id_ != other.id_)
			return false;
		return true;
	}

	@Override
	public Node[] getNodes() {
		return edgeNodes_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id_ ^ (id_ >>> 32));
		return result;
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

	@Override
	protected void readFullObject(ObjectInput in) throws IOException,
			ClassNotFoundException {
		edgeNodes_ = (Node[]) in.readObject();
	}

	@Override
	protected void writeFullObject(ObjectOutput out) throws IOException {
		out.writeObject(edgeNodes_);
	}
}
