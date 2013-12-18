/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core.cli.comparator;

import util.UniqueID;

public class IDComparator extends DefaultComparator {
	@Override
	protected int compareInternal(Object o1, Object o2) {
		// Need to be identifiable
		if (o1 instanceof UniqueID && o2 instanceof UniqueID)
			return Long.compare(((UniqueID) o1).getID(),
					((UniqueID) o2).getID());

		return 0;
	}
}
