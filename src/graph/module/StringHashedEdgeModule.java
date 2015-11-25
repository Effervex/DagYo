/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sam Sarjant - initial API and implementation
 ******************************************************************************/
package graph.module;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.Edge;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class should be used in conjunction with RelatedEdgeModule as a faster
 * method of filtering the results for queries involving strings. Note that it
 * cannot be added to directly, all additions take place as side-effects of
 * interacting with the results of the execute method.
 * 
 * @author Sam Sarjant
 */
public class StringHashedEdgeModule extends DAGModule<Collection<Edge>> {
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_CAPACITY = 65536;
	protected TIntObjectMap<Collection<Edge>> stringHashMap_;

	public StringHashedEdgeModule() {
		this(DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public StringHashedEdgeModule(int capacity) {
		stringHashMap_ = new TIntObjectHashMap<>(capacity);
	}

	@Override
	public void clear() {
		stringHashMap_.clear();
	}

	@Override
	public Collection<Edge> execute(Object... args)
			throws IllegalArgumentException, ModuleException {
		return locateEdgeCollection((Boolean) args[0], (String) args[1]);
	}

	/**
	 * Returns the collection associated with the string, creating a new
	 * collection if createNew is true.
	 *
	 * @param createNew
	 *            If a new collection should be added at the hash location.
	 * @param str
	 *            The string to access.
	 * @return The edge collection at the given string hash.
	 */
	@SuppressWarnings("unchecked")
	public synchronized Collection<Edge> locateEdgeCollection(
			boolean createNew, String str) {
		int hash = str.hashCode();
		Collection<Edge> edges = stringHashMap_.get(hash);
		if (edges == null) {
			if (createNew) {
				edges = Collections
						.newSetFromMap(new ConcurrentHashMap<Edge, Boolean>());
				stringHashMap_.put(hash, edges);
			} else {
				return Collections.EMPTY_SET;
			}
		}
		return edges;
	}

	@Override
	public boolean supportsEdge(DAGEdge edge) {
		return true;
	}

	@Override
	public boolean supportsNode(DAGNode node) {
		return false;
	}
}
