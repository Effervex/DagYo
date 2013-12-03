package graph.core.cli;

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Identifiable;
import graph.core.Node;
import graph.core.PrimitiveNode;
import graph.core.StringNode;
import graph.core.cli.comparator.DefaultComparator;
import graph.core.cli.comparator.IDComparator;
import graph.core.cli.comparator.StringCaseInsComparator;
import graph.core.cli.comparator.StringComparator;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.UtilityMethods;
import core.CommandQueue;
import core.PortHandler;

public class DAGPortHandler extends PortHandler {
	public static final String DYNAMICALLY_ADD_NODES = "/env/edgesAddNodes";
	public static final String PRETTY_RESULTS = "/env/pretty";
	public static final String SORT_ORDER = "/env/sort";
	protected DirectedAcyclicGraph dag_;

	public DAGPortHandler(Socket aSocket, CommandQueue aQueue,
			DirectedAcyclicGraph dag) {
		super(aSocket, aQueue);
		set(SORT_ORDER, "default");
		dag_ = dag;
	}

	public DirectedAcyclicGraph getDAG() {
		return dag_;
	}

	public String textIDObject(Identifiable obj) {
		if (get(PRETTY_RESULTS).equals("true"))
			return obj.getIdentifier() + ":" + obj.toString();
		else if (get(PRETTY_RESULTS).equals("only"))
			return obj.toString();
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

	/**
	 * Sorts a collection by the collection defined in the variables.
	 * 
	 * @param items
	 *            The collection to be sorted.
	 * @return A sorted collection, or the same collection if no sorter
	 *         defined/defined as default.
	 */
	public final <T> Collection<T> sort(Collection<T> items, int start, int end) {
		if (items == null || items.isEmpty())
			return items;
		DefaultComparator comparator = getComparator();

		// Range values
		start = Math.max(0, start);
		end = Math.min(items.size(), end);

		if (comparator != null) {
			comparator.setHandler(this);
			List<T> sortedNodes = new ArrayList<>(items);
			Collections.sort(sortedNodes, comparator);
			if (start != 0 || end != items.size())
				sortedNodes = sortedNodes.subList(start, end);
			return sortedNodes;
		}

		// Trim the results
		if (start != 0 || end != items.size()) {
			Collection<T> trimmedItems = new ArrayList<T>(end - start);
			Iterator<T> iter = items.iterator();
			for (int i = 0; i < end; i++) {
				T item = iter.next();
				if (i >= start)
					trimmedItems.add(item);
			}
			return trimmedItems;
		}

		return items;
	}

	/**
	 * Returns a comparator based on the sort variable.
	 * 
	 * @return The comparator based on the sorter.
	 */
	protected DefaultComparator getComparator() {
		DefaultComparator comparator = null;
		if (get(SORT_ORDER).equals("id"))
			comparator = new IDComparator();
		else if (get(SORT_ORDER).equals("alpha"))
			comparator = new StringComparator();
		else if (get(SORT_ORDER).equals("alphaNoCase"))
			comparator = new StringCaseInsComparator();
		return comparator;
	}

	public Object convertToComparable(Object o) {
		return o;
	}
}
