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
package graph.core.cli;

import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;
import graph.core.Identifiable;
import graph.core.cli.comparator.AliasedNodesComparator;
import graph.core.cli.comparator.IDComparator;
import graph.core.cli.comparator.StringCaseInsComparator;
import graph.core.cli.comparator.StringComparator;
import graph.core.cli.filters.SubDAGFilter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.Predicate;

import util.AliasedObject;
import core.CommandQueue;
import core.PortHandler;

public class DAGPortHandler extends PortHandler {
	public static final String DYNAMICALLY_ADD_NODES = "/env/edgesAddNodes";
	public static final String PRETTY_RESULTS = "/env/pretty";
	public static final String SORT_ORDER = "/env/sort";
	public static final String EDGE_FLAGS = "/env/edgeFlags";
	public static final String NODE_FLAGS = "/env/nodeFlags";
	public static final String SUBDAG_FILTERING = "/env/subDAGFilter";
	public static final String HUMAN = "/human";
	protected DirectedAcyclicGraph dag_;

	public DAGPortHandler(Socket aSocket, CommandQueue aQueue,
			DirectedAcyclicGraph dag) {
		super(aSocket, aQueue);
		if (get(SORT_ORDER) == null || get(SORT_ORDER).isEmpty())
			set(SORT_ORDER, "default");
		if (get(EDGE_FLAGS) == null || get(EDGE_FLAGS).isEmpty())
			set(EDGE_FLAGS, "");
		if (get(NODE_FLAGS) == null || get(NODE_FLAGS).isEmpty())
			set(NODE_FLAGS, "");
		if (get(SUBDAG_FILTERING) == null || get(SUBDAG_FILTERING).isEmpty())
			set(SUBDAG_FILTERING, "");
		if (get(HUMAN) == null || get(HUMAN).isEmpty())
			set(HUMAN, "false");
		dag_ = dag;
	}

	public DirectedAcyclicGraph getDAG() {
		return dag_;
	}

	public String textIDObject(Identifiable obj) {
		if (get(PRETTY_RESULTS) == null)
			return obj.getIdentifier();
		if (get(PRETTY_RESULTS).equals("true"))
			return obj.getIdentifier() + ":" + obj.toString();
		else if (get(PRETTY_RESULTS).equals("only"))
			return obj.toString();
		else
			return obj.getIdentifier();
	}

	/**
	 * Processes a collection of objects that are the result of prior
	 * operations. This includes sorting, filtering, and subsetting.
	 * 
	 * @param items
	 *            The collection to be sorted.
	 * @param start
	 *            The start index to return (inclusive).
	 * @param end
	 *            The end index to keep (exclusive).
	 * @param sort
	 *            If the items should be sorted.
	 * @return A sorted collection, or the same collection if no sorter
	 *         defined/defined as default.
	 */
	public final <T> Collection<T> postProcess(Collection<T> items, int start,
			int end, boolean sort) {
		if (items == null || items.isEmpty())
			return items;
		Collection<Predicate<Object>> filters = getFilters();

		// Filter
		List<T> output = new ArrayList<T>();
		for (T item : items) {
			boolean keep = true;
			for (Predicate<Object> filter : filters) {
				if (!filter.evaluate(item)) {
					keep = false;
					break;
				}
			}

			if (keep)
				output.add(item);
		}

		// Sort
		if (sort) {
			Comparator<Object> comparator = getComparator();
			if (comparator != null)
				Collections.sort(output, comparator);
		}

		// Trim
		start = Math.max(0, start);
		end = Math.min(output.size(), end);
		if (start != 0 || end != items.size())
			output = output.subList(start, end);
		return output;
	}

	protected Collection<Predicate<Object>> getFilters() {
		Collection<Predicate<Object>> filters = new ArrayList<>();
		String subDagFilter = get(SUBDAG_FILTERING);
		if (subDagFilter != null && !subDagFilter.isEmpty()) {
			filters.add(new SubDAGFilter(subDagFilter, this));
		}
		return filters;
	}

	/**
	 * Returns a comparator based on the sort variable.
	 * 
	 * @return The comparator based on the sorter.
	 */
	public Comparator<Object> getComparator() {
		Comparator<Object> comparator = null;
		if (get(SORT_ORDER).equals("id"))
			comparator = new IDComparator(this);
		else if (get(SORT_ORDER).equals("alpha"))
			comparator = new StringComparator(this);
		else if (get(SORT_ORDER).equals("alphaNoCase"))
			comparator = new StringCaseInsComparator(this);
		else if (get(SORT_ORDER).equals("alias"))
			comparator = new AliasedNodesComparator(this, null);
		return comparator;
	}

	@SuppressWarnings("rawtypes")
	public Object convertToComparable(Object o) {
		if (o instanceof AliasedObject) {
			return ((AliasedObject) o).getAliasString();
		}
		return o;
	}

	public boolean[] asBooleanArray(String variableKey) {
		String value = get(variableKey);
		if (value == null)
			value = "";
		value = value.toLowerCase();
		boolean[] array = new boolean[value.length()];
		for (int i = 0; i < array.length; i++)
			array[i] = value.charAt(i) == 't';
		return array;
	}

	public DAGObject convertToDAGObject(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof AliasedObject)
			obj = ((AliasedObject) obj).object_;
		if (obj instanceof DAGObject)
			return (DAGObject) obj;
		else
			return null;
	}
}
