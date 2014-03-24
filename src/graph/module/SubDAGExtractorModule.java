package graph.module;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import util.collection.MultiMap;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.Node;
import graph.core.StringNode;

/**
 * Extracts a subDAG form this DAG, using tagged nodes and edges.
 * 
 * @author Sam Sarjant
 */
public class SubDAGExtractorModule extends DAGModule<Boolean> {
	private static final long serialVersionUID = 1L;
	public static final String TAG_PREFIX = "subDAG";
	private MultiMap<String, DAGNode> taggedNodes_;

	public SubDAGExtractorModule() {
		taggedNodes_ = MultiMap.createListMultiMap();
	}

	@Override
	public boolean initialisationComplete(Collection<DAGNode> nodes,
			Collection<DAGEdge> edges, boolean forceRebuild) {
		if (taggedNodes_ == null)
			taggedNodes_ = MultiMap.createListMultiMap();
		return super.initialisationComplete(nodes, edges, forceRebuild);
	}

	@Override
	public Boolean execute(Object... args) throws IllegalArgumentException,
			ModuleException {
		if (args.length != 3)
			return false;
		return extractSubDAG((File) args[0], (String) args[1], (int) args[2]);
	}

	public boolean extractSubDAG(File folder, String tag, int distance) {
		tag = TAG_PREFIX + tag;
		RelatedEdgeModule relatedEdgeModule = (RelatedEdgeModule) dag_
				.getModule(RelatedEdgeModule.class);

		try {
			// Create new DAG
			DirectedAcyclicGraph subDAG = createNewDAG(folder);
			subDAG.initialise();

			// Identify nodes
			Collection<DAGNode> nodes = new HashSet<>(taggedNodes_.get(tag));

			// Follow edges by distance to produce more nodes
			followEdges(nodes, distance, relatedEdgeModule);

			// Identify edges using nodes
			Collection<DAGEdge> edges = findLinks(nodes, relatedEdgeModule);

			// Assert
			StringNode creator = new StringNode(tag);
			for (DAGNode node : nodes)
				subDAG.findOrCreateNode(node.getName(), creator, true);
			for (DAGEdge edge : edges) {
				Node[] subDAGNodes = subDAG.parseNodes(edge.toString(false),
						creator, false, false);
				subDAG.findOrCreateEdge(creator, subDAGNodes, true);
			}

			// Serialise to folder
			subDAG.saveState();

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Finds all edges which involve the nodes as arguments. There cannot be any
	 * edges that introduce new DAGNodes (though non-DAG nodes are fine). With
	 * the exception of introducing predicates that define the edge.
	 * 
	 * @param nodes
	 *            The nodes to find edges for.
	 * @return The collection of edges linking these nodes
	 */
	public Collection<DAGEdge> findLinks(Collection<DAGNode> nodes,
			RelatedEdgeModule relatedEdgeModule) {
		Collection<DAGEdge> linkedEdges = new HashSet<>();
		Collection<DAGNode> predicates = new HashSet<>();
		// For every node
		for (Node n : nodes) {
			Collection<Edge> relatedEdges = relatedEdgeModule.execute(n, -1, n);
			// Check every edge
			for (Edge e : relatedEdges) {
				Node[] args = e.getNodes();
				boolean addEdge = true;
				for (int i = 1; i < args.length; i++) {
					if (args[i] instanceof DAGNode && !nodes.contains(args[i])) {
						addEdge = false;
						break;
					}
				}

				if (addEdge) {
					linkedEdges.add((DAGEdge) e);

					// Add the predicate
					predicates.add((DAGNode) args[0]);
				}
			}
		}
		nodes.addAll(predicates);
		return linkedEdges;
	}

	/**
	 * Follow all edges related to a group of nodes by a given distance.
	 * 
	 * @param nodes
	 *            The nodes to start from.
	 * @param distance
	 *            The distance to travel.
	 * @param relatedEdgeModule
	 *            The related edge module.
	 */
	public void followEdges(Collection<DAGNode> nodes, int distance,
			RelatedEdgeModule relatedEdgeModule) {
		// Move through the distance in a breadth-first fashion (level-by-level)
		Collection<DAGNode> completed = new HashSet<>();
		Collection<DAGNode> currentLevel = nodes;
		for (int d = distance; d > 0; d--) {
			Collection<DAGNode> nextLevel = new HashSet<>();
			// For every node on this level
			for (DAGNode n : currentLevel) {
				// Find linked edges.
				Collection<Edge> relatedEdges = relatedEdgeModule.execute(n,
						-1, n);
				for (Edge e : relatedEdges) {
					// Grab every non-predicate argument
					Node[] args = e.getNodes();
					for (int i = 1; i < args.length; i++) {
						// Add to next level if not already looked at
						if (args[i] instanceof DAGNode
								&& !completed.contains(args[i]))
							nextLevel.add((DAGNode) args[i]);
					}
				}
			}
			currentLevel = nextLevel;
			// Add all the next level nodes
			nodes.addAll(nextLevel);
		}
	}

	protected DirectedAcyclicGraph createNewDAG(File folder) {
		DirectedAcyclicGraph dag = new DirectedAcyclicGraph(folder);
		return dag;
	}

	public synchronized void tagDAGObject(DAGObject dagObj, String tag) {
		String alterTag = TAG_PREFIX + tag;
		if (dagObj instanceof DAGNode) {
			dag_.addProperty(dagObj, alterTag, "T");
			taggedNodes_.put(alterTag, (DAGNode) dagObj);
		} else if (dagObj instanceof DAGEdge) {
			for (Node n : ((DAGEdge) dagObj).getNodes()) {
				if (n instanceof DAGNode)
					tagDAGObject((DAGObject) n, tag);
			}
		}
	}

}
