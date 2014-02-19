package graph.core;

public class NonExistentErrorEdge extends DAGErrorEdge {
	private static final long serialVersionUID = 1L;
	private Node node_;

	public NonExistentErrorEdge(Node n) {
		node_ = n;
	}

	@Override
	public String getError() {
		return "The node " + node_ + " does not exist.";
	}

	@Override
	public Node[] getNodes() {
		return new Node[] { node_ };
	}

}
