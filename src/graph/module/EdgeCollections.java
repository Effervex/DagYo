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
