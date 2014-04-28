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
package graph.core.cli.comparator;

import graph.core.DAGObject;
import graph.core.cli.DAGPortHandler;

import java.util.Comparator;

public abstract class DefaultComparator implements Comparator<Object> {
	private DAGPortHandler handler_;

	public DefaultComparator(DAGPortHandler handler) {
		handler_ = handler;
	}

	@Override
	public final int compare(Object o1, Object o2) {
		if (o1 == null)
			if (o2 == null)
				return 0;
			else
				return 1;
		else if (o2 == null)
			return -1;

		// Convert where appropriate
		Object comp1 = o1;
		Object comp2 = o2;
		if (o1.getClass().equals(o2.getClass())) {
			comp1 = handler_.convertToComparable(o1);
			comp2 = handler_.convertToComparable(o2);
		}

		// Perform internal check
		int result = compareInternal(comp1, comp2);
		if (result != 0)
			return result;

		if (!(o1 instanceof DAGObject && o2 instanceof DAGObject)) {
			DAGObject dag1 = handler_.convertToDAGObject(o1);
			DAGObject dag2 = handler_.convertToDAGObject(o2);
			if (dag1 != null && dag2 != null) {
				result = compareInternal(dag1, dag2);
				if (result != 0)
					return result;
			}
		}

		// Default to hashCode and classname comparison
		result = Integer.compare(o1.hashCode(), o2.hashCode());
		if (result != 0)
			return result;

		return o1.getClass().getCanonicalName()
				.compareTo(o2.getClass().getCanonicalName());
	}

	protected abstract int compareInternal(Object o1, Object o2);
}
