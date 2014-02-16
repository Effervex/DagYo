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

import graph.core.Edge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EdgeCollections {
	private List<Collection<Edge>> removedEdges_;
	private List<Collection<Edge>> retainedEdges_;

	public void add(Collection<Edge> edges, boolean retained) {
		if (retained)
			getRetainedEdges().add(edges);
		else
			getRemovedEdges().add(edges);
	}

	public List<Collection<Edge>> getRemovedEdges() {
		if (removedEdges_ == null)
			removedEdges_ = new ArrayList<>();
		return removedEdges_;
	}

	public List<Collection<Edge>> getRetainedEdges() {
		if (retainedEdges_ == null)
			retainedEdges_ = new ArrayList<>();
		return retainedEdges_;
	}

	public void add(EdgeCollections otherEdgeCols) {
		getRetainedEdges().addAll(otherEdgeCols.getRetainedEdges());
		getRemovedEdges().addAll(otherEdgeCols.getRemovedEdges());
	}
}
