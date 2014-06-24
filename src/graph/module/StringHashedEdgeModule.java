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

import graph.core.Edge;

import java.util.Collection;
import java.util.HashSet;

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
	protected Collection<Edge>[] stringHashArray_;
	private boolean requiresRebuild_ = true;

	@SuppressWarnings("unchecked")
	public StringHashedEdgeModule(int capacity) {
		stringHashArray_ = new Collection[capacity];
	}

	public StringHashedEdgeModule() {
		this(DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		stringHashArray_ = new Collection[stringHashArray_.length];
	}

	@Override
	public Collection<Edge> execute(Object... args)
			throws IllegalArgumentException, ModuleException {
		if (args == null || args.length < 1)
			return null;

		int hash = getHash(args[0]);
		Collection<Edge> edges = stringHashArray_[hash];
		if (edges == null) {
			edges = new HashSet<>();
			stringHashArray_[hash] = edges;
		}
		return edges;
	}

	private int getHash(Object o) {
		int hash = o.toString().hashCode() % stringHashArray_.length;
		if (hash < 0)
			hash *= -1;
		return hash;
	}
}
