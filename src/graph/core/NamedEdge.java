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
