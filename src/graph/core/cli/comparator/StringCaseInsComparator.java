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

public class StringCaseInsComparator extends DefaultComparator {
	@Override
	protected int compareInternal(Object o1, Object o2) {
		return o1.toString().toLowerCase()
				.compareTo(o2.toString().toLowerCase());
	}

}
