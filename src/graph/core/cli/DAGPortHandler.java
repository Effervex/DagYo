package graph.core.cli;

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Identifiable;
import graph.core.Node;
import graph.core.PrimitiveNode;
import graph.core.StringNode;

import java.net.Socket;

import util.UtilityMethods;
import core.CommandQueue;
import core.PortHandler;

public class DAGPortHandler extends PortHandler {
	public static final String DYNAMICALLY_ADD_NODES = "/env/edgesAddNodes";
	public static final String PRETTY_RESULTS = "/env/pretty";
	protected DirectedAcyclicGraph dag_;

	public DAGPortHandler(Socket aSocket, CommandQueue aQueue,
			DirectedAcyclicGraph dag) {
		super(aSocket, aQueue);
		dag_ = dag;
	}

	public DirectedAcyclicGraph getDAG() {
		return dag_;
	}

	public String textIDObject(Identifiable obj) {
		if (get(PRETTY_RESULTS).equals("true"))
			return obj.getIdentifier() + ":" + obj.toString();
		else
			return obj.getIdentifier();
	}

	public static Node createNode(String name, Node creator) {
		if (name == null)
			return new DAGNode(creator);

		// Parse string
		if (name.startsWith("\"\"") && name.endsWith("\"\""))
			return new StringNode(UtilityMethods.shrinkString(name, 2));

		// Parse primitive
		PrimitiveNode prim = PrimitiveNode.parseNode(name);
		if (prim != null)
			return prim;

		// Default to DAG
		return new DAGNode(name, creator);
	}
}
