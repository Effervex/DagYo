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

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import util.collection.trove.TIndexedCollection;
import util.serialisation.DefaultSerialisationMechanism;
import util.serialisation.SerialisationMechanism;

/**
 * A DAG module defines the basic framework a DAG module must conform to.
 * 
 * @author Sam Sarjant
 */
public abstract class DAGModule<T> implements Serializable {
	protected static final String MODULE_DIR = "modules";
	private static final long serialVersionUID = -1752235659675219252L;
	protected transient DirectedAcyclicGraph dag_;

	// TODO Add capability to cache

	protected DAGModule() {
	}

	protected void defaultRebuild(TIndexedCollection<DAGNode> nodes,
			boolean iterateNodes, TIndexedCollection<DAGEdge> edges,
			boolean iterateEdges) {
		if (iterateNodes) {
			DAGNode[] nodeArray = nodes.toArray(new DAGNode[nodes.size()]);
			for (DAGNode node : nodeArray) {
				if (supportsNode(node))
					addNode(node);
			}
		}
		if (iterateEdges) {
			DAGEdge[] edgeArray = edges.toArray(new DAGEdge[edges.size()]);
			for (DAGEdge edge : edgeArray) {
				if (supportsEdge(edge))
					addEdge(edge);
			}
		}
	}

	/**
	 * Called after an edge is added to the DAG.
	 * 
	 * @param edge
	 *            The edge that was added.
	 * @return False if the edge should be removed.
	 */
	public boolean addEdge(DAGEdge edge) {
		return true;
	}

	/**
	 * Called after a node is added to the DAG.
	 * 
	 * @param node
	 *            The node that was added.
	 * @return False if the node should be removed.
	 */
	public boolean addNode(DAGNode node) {
		return true;
	}

	public void addProperty(DAGObject dagObj, String key, String value) {

	}

	public void clear() {

	}

	/**
	 * Removes any cached information the module has, requiring it to compute
	 * results on demand.
	 */
	public void disableCached() {
	}

	public abstract T execute(Object... args) throws IllegalArgumentException,
			ModuleException;

	/**
	 * Gets any pertinent properties the module uses on nodes and edges.
	 * 
	 * @return The collection of all pertinent properties used.
	 */
	public Collection<String> getPertinentProperties() {
		return new ArrayList<String>(0);
	}

	/**
	 * A method that is called once initialisation of a DAG is complete. Note
	 * that this method may not be called, and no functionality is required.
	 * 
	 * @param nodes
	 *            The collection of all existing nodes.
	 * @param edges
	 *            The collection of all existing edges.
	 * @param forceRebuild
	 *            If the modules should be forcibly rebuilt (even if they are
	 *            up-to-date).
	 * @return If something changed.
	 */
	public boolean initialisationComplete(TIndexedCollection<DAGNode> nodes,
			TIndexedCollection<DAGEdge> edges, boolean forceRebuild) {
		return false;
	}

	/**
	 * Called after 'edge' is removed.
	 * 
	 * @param edge
	 *            The edge being removed.
	 * @return Returns boolean (no default meaning).
	 */
	public boolean removeEdge(DAGEdge edge) {
		return false;
	}

	/**
	 * Called after 'node' is removed.
	 * 
	 * @param node
	 *            The node being removed.
	 * @return Returns boolean (no default meaning).
	 */
	public boolean removeNode(DAGNode node) {
		return false;
	}

	/**
	 * Removes a property from a DAG Object.
	 * 
	 * @param dagObj
	 *            The object removing the property.
	 * @param key
	 *            The key being removed.
	 */
	public void removeProperty(DAGObject dagObj, String key) {

	}

	public boolean saveModule(File rootDir) {
		DefaultSerialisationMechanism serialiser = SerialisationMechanism.FST
				.getSerialiser();
		File modFile = moduleFile(rootDir, getClass().getSimpleName());
		try {
			modFile.createNewFile();
			// If a module should only save IDs for the nodes/edges.
			serialiser.serialize(this, modFile,
					DefaultSerialisationMechanism.ID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void setDAG(DirectedAcyclicGraph directedAcyclicGraph) {
		dag_ = directedAcyclicGraph;
	}

	/**
	 * If this module supports add/remove operations with the given edge. Some
	 * edges may use special modifiers that make them incompatible with the
	 * module. This is separate from execution, which does not use this method.
	 *
	 * @param edge
	 *            The edge to be added/removed to/from the module.
	 * @return True if the edge is supported, false otherwise.
	 */
	public abstract boolean supportsEdge(DAGEdge edge);

	/**
	 * If this module supports add/remove operations with the given node. Some
	 * nodes may use special modifiers that make them incompatible with the
	 * module. This is separate from execution, which does not use this method.
	 *
	 * @param node
	 *            The node to be added/removed to/from the module.
	 * @return True if the node is supported, false otherwise.
	 */
	public abstract boolean supportsNode(DAGNode node);

	protected static File moduleFile(File rootDir, String moduleName) {
		File file = new File(rootDir, MODULE_DIR + File.separatorChar
				+ moduleName);
		file.getParentFile().mkdirs();
		return file;
	}

	public static DAGModule<?> loadCreateModule(File rootDir,
			Class<?> moduleClass) throws InstantiationException,
			IllegalAccessException {
		DefaultSerialisationMechanism serialiser = SerialisationMechanism.FST
				.getSerialiser();
		File modFile = moduleFile(rootDir, moduleClass.getSimpleName());
		if (modFile.exists()) {
			try {
				DAGModule<?> module = (DAGModule<?>) serialiser
						.deserialize(modFile);
				return module;
			} catch (Exception e) {
				System.err
						.println("Could not deserialize module. Creating a new one.");
			}
		}

		// TODO In this case, need to rescan dirs.
		return (DAGModule<?>) moduleClass.newInstance();
	}
}
