package graph.core;

public class NonExistentNodeException extends DAGException {

	private static final long serialVersionUID = -6665757721192481789L;

	public NonExistentNodeException(Node n, Node[] edgeNodes) {
		super("Node " + n + " has not yet been created for edge: " + edgeNodes);
	}

}
