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
package graph.core;

public abstract class DAGErrorEdge implements ErrorEdge {
	private static final long serialVersionUID = 1L;

	@Override
	public final boolean containsNode(Node node) {
		return false;
	}

	@Override
	public final String toString(boolean useIDs) {
		return getError();
	}

	public final String toString() {
		return getError();
	}

	@Override
	public final String getIdentifier() {
		return getError();
	}

	@Override
	public final String getIdentifier(boolean useName) {
		return getError();
	}

	@Override
	public final int getID() {
		return -1;
	}
}
