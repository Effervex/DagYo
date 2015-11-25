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
package graph.module;

import gnu.trove.iterator.TIntObjectIterator;
import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;
import graph.core.Node;
import graph.core.StringNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.collection.MultiMap;
import util.collection.trove.TIndexedCollection;

/**
 * Extracts a subDAG form this DAG, using tagged nodes and edges.
 * 
 * @author Sam Sarjant
 */
public class SubDAGExtractorModule extends DAGModule<Boolean> {
	private static Logger logger = LoggerFactory
			.getLogger(SubDAGExtractorModule.class);
	private static final long serialVersionUID = 1L;
	/** The prefix used for grown tags. */
	public static final String NON_CORE_PREFIX = "subDAGNC";
	/** The prefix used for user-tagged concepts. */
	public static final String TAG_PREFIX = "subDAG";
	private transient MultiMap<String, DAGNode> taggedNodes_;

	public SubDAGExtractorModule() {
		taggedNodes_ = MultiMap.createSortedSetMultiMap();
	}

	private boolean getSubNodesAndEdges(String tag, int distance,
			Collection<DAGEdge> edges, Collection<DAGNode> nodes) {
		RelatedEdgeModule relatedEdgeModule = (RelatedEdgeModule) dag_
				.getModule(RelatedEdgeModule.class);

		if (!taggedNodes_.containsKey(tag))
			return false;

		// 1. Identify tagged nodes
		Collection<DAGNode> newlyAddedNodes = new HashSet<>(
				taggedNodes_.get(tag));
		logger.debug("Identify tagged nodes: {}", newlyAddedNodes);

		boolean loop = false;
		do {
			// 2. Find links/ancestry of tagged nodes
			logger.debug("Find links/ancestry of tagged nodes {}", distance);
			findLinks(newlyAddedNodes, nodes, distance, relatedEdgeModule);
			logger.debug("Number of nodes: {}", newlyAddedNodes.size());

			// 3. Find other links between new nodes
			logger.debug("Linking nodes");
			edges.addAll(incorporateNewAndLinkEdges(nodes, newlyAddedNodes,
					relatedEdgeModule));
			logger.debug("Found {} linking edges.", edges.size());

			loop = !newlyAddedNodes.isEmpty();
			distance = 0;
		} while (loop);
		return true;
	}

	protected DirectedAcyclicGraph createNewDAG(File folder) {
		DirectedAcyclicGraph dag = new DirectedAcyclicGraph(folder, null, null);
		return dag;
	}

	protected void preAssertionProcessing(Collection<DAGNode> newlyAddedNodes,
			Collection<DAGNode> processedNodes, Collection<DAGEdge> edges) {
	}

	@Override
	public Boolean execute(Object... args) throws IllegalArgumentException,
			ModuleException {
		if (args.length != 3)
			return false;
		return extractSubDAG((File) args[0], (String) args[1], (int) args[2]);
	}

	/**
	 * Extracts a subDAG from the DAG, saving it into a given folder.
	 * 
	 * @param folder
	 *            The folder in which to save the subDAG.
	 * @param tag
	 *            The tag to extract the subDAG from.
	 * @param distance
	 *            The distance in which the subDAG reaches from tagged nodes.
	 * @return True.
	 */
	public boolean extractSubDAG(File folder, String tag, int distance) {
		tag = TAG_PREFIX + tag;

		// Create new DAG
		// TODO This seems like it would create a whole new DAG!
		logger.debug("Creating SubDAG");
		DirectedAcyclicGraph subDAG = createNewDAG(folder);
		subDAG.initialise();

		Collection<DAGEdge> edges = new HashSet<>();
		Collection<DAGNode> nodes = new HashSet<>();

		// Find the relevant nodes and edges.
		if (!getSubNodesAndEdges(tag, distance, edges, nodes))
			return false;

		// Assert
		StringNode creator = new StringNode(tag);
		logger.debug("Asserting {} nodes.", nodes.size());
		for (DAGNode node : nodes)
			subDAG.findOrCreateNode(node.getName(), creator, true);
		logger.debug("Asserting {} edges.", edges.size());
		for (DAGEdge edge : edges) {
			Node[] edgeNodes = edge.getNodes();
			Node[] newEdgeNodes = new Node[edgeNodes.length];
			for (int i = 0; i < edgeNodes.length; i++) {
				newEdgeNodes[i] = subDAG
						.findOrCreateNode(edgeNodes[i].getIdentifier(true),
								creator, false, false);
			}
			subDAG.findOrCreateEdge(newEdgeNodes, creator, true);
		}

		// Serialise to folder
		logger.debug("Saving state.");
		subDAG.saveState();

		return true;
	}

	/**
	 * Follow all edges related to a group of nodes by a given distance.
	 * 
	 * @param nodes
	 *            The nodes to start from.
	 * @param existingNodes
	 * @param distance
	 *            The distance to travel.
	 * @param relatedEdgeModule
	 *            The related edge module.
	 */
	public void findLinks(Collection<DAGNode> nodes,
			Collection<DAGNode> existingNodes, int distance,
			RelatedEdgeModule relatedEdgeModule) {
		if (distance == 0)
			return;

		// Move through the distance in a breadth-first fashion (level-by-level)
		Collection<DAGNode> completed = new HashSet<>(existingNodes);
		completed.addAll(nodes);
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

	public Collection<DAGNode> getTagged(String tag) {
		String alterTag = TAG_PREFIX + tag;
		Collection<DAGNode> allTagged = new ArrayList<>();
		if (taggedNodes_.containsKey(alterTag))
			allTagged.addAll(taggedNodes_.get(alterTag));
		String nonCoreTag = NON_CORE_PREFIX + tag;
		if (taggedNodes_.containsKey(nonCoreTag))
			allTagged.addAll(taggedNodes_.get(nonCoreTag));
		return allTagged;
	}

	/**
	 * This method only increases the reach of tagged nodes by distance (as well
	 * as any other effects that normally apply).
	 * 
	 * @param tag
	 *            The tag to grow from.
	 * @param distance
	 *            The distance to grow by.
	 * @return True.
	 */
	public boolean growSubDAG(String tag, int distance) {
		Collection<DAGEdge> edges = new HashSet<>();
		Collection<DAGNode> nodes = new HashSet<>();
		// Find the relevant nodes and edges
		if (!getSubNodesAndEdges(TAG_PREFIX + tag, distance, edges, nodes))
			return false;

		for (Node n : nodes) {
			if (n instanceof DAGNode)
				tagDAGObject((DAGNode) n, tag, false);
		}
		return true;
	}

	/**
	 * Finds all edges which involve the nodes as arguments. There cannot be any
	 * edges that introduce new DAGNodes (though non-DAG nodes are fine). With
	 * the exception of introducing predicates that define the edge.
	 * 
	 * @param linkedNodes
	 *            The nodes that have already been linked up together
	 * @param toBeLinkedNodes
	 *            The nodes to be added and linked up.
	 * @param relatedEdgeModule
	 *            The Related Edge Module access.
	 * @return The collection of edges linking these nodes
	 */
	public Collection<DAGEdge> incorporateNewAndLinkEdges(
			Collection<DAGNode> linkedNodes,
			Collection<DAGNode> toBeLinkedNodes,
			RelatedEdgeModule relatedEdgeModule) {
		Collection<DAGEdge> linkedEdges = new HashSet<>();
		boolean addPredicate = true;

		// Incorporate the new nodes into the linked nodes
		linkedNodes.addAll(toBeLinkedNodes);

		// Link up the newly incorporated nodes with edges.
		Collection<DAGNode> predicates = new HashSet<>();
		for (DAGNode newNode : toBeLinkedNodes) {
			Collection<Edge> relatedEdges = relatedEdgeModule.execute(newNode,
					-1, newNode);
			// Check every edge
			for (Edge e : relatedEdges) {
				if (linkedEdges.contains(e))
					continue;
				Node[] args = e.getNodes();
				boolean addEdge = true;
				for (int i = 1; i < args.length; i++) {
					if (args[i] instanceof DAGNode
							&& !linkedNodes.contains(args[i])) {
						addEdge = false;
						break;
					}
				}

				if (addEdge) {
					if (addPredicate || linkedNodes.contains(args[0])) {
						// Add the predicate
						if (!linkedNodes.contains(args[0]))
							predicates.add((DAGNode) args[0]);

						linkedEdges.add((DAGEdge) e);
					}
				}
			}
		}

		// Store predicates as to-be-linked
		toBeLinkedNodes.clear();
		toBeLinkedNodes.addAll(predicates);
		return linkedEdges;
	}

	@Override
	public boolean initialisationComplete(TIndexedCollection<DAGNode> nodes,
			TIndexedCollection<DAGEdge> edges, boolean forceRebuild) {
		if (taggedNodes_ == null) {
			taggedNodes_ = MultiMap.createSortedSetMultiMap();
			TIntObjectIterator<DAGNode> iter = nodes.iterator();
			for (int i = nodes.size(); i-- > 0;) {
				iter.advance();
				DAGNode n = iter.value();
				String[] props = n.getProperties();
				for (String prop : props) {
					if (prop.startsWith(TAG_PREFIX)) {
						taggedNodes_.put(prop, n);
					}
				}
			}
		}
		return super.initialisationComplete(nodes, edges, forceRebuild);
	}

	public boolean isTagged(DAGNode node, String tag) {
		String coreTag = TAG_PREFIX + tag;
		String nonCoreTag = NON_CORE_PREFIX + tag;
		return node.getProperty(coreTag) != null
				|| node.getProperty(nonCoreTag) != null;
	}

	public synchronized void removeTagDAGObject(DAGNode dagObj, String tag) {
		String[] coreAndNonCore = { TAG_PREFIX + tag, NON_CORE_PREFIX + tag };
		for (String alterTag : coreAndNonCore) {
			dag_.removeProperty(dagObj, alterTag);
			Collection<DAGNode> tagged = taggedNodes_.get(alterTag);
			if (tagged != null) {
				taggedNodes_.get(alterTag).remove(dagObj);
				if (taggedNodes_.isValueEmpty(alterTag))
					taggedNodes_.remove(alterTag);
			}
		}
	}

	@Override
	public boolean supportsEdge(DAGEdge edge) {
		return false;
	}

	@Override
	public boolean supportsNode(DAGNode node) {
		return false;
	}

	public synchronized void tagDAGObject(DAGNode dagObj, String tag,
			boolean coreTag) {
		String alterTag = TAG_PREFIX + tag;
		String nonCoreTag = NON_CORE_PREFIX + tag;
		String appliedTag = (coreTag) ? alterTag : nonCoreTag;
		if (coreTag && dagObj.getProperty(nonCoreTag) != null) {
			// Upgrade non-core to core
			removeTagDAGObject(dagObj, tag);
		} else if (!coreTag && dagObj.getProperty(alterTag) != null)
			// Do not revert to non-core
			return;

		dag_.addProperty(dagObj, appliedTag, "T");
		taggedNodes_.put(appliedTag, dagObj);
	}

	@Override
	public String toString() {
		if (taggedNodes_ == null)
			return "SubDAG Extractor Module (not yet initialised)";
		return "SubDAG Extractor Module: " + taggedNodes_.sizeTotal()
				+ " tagged nodes from " + taggedNodes_.size() + " tags";
	}
}
