package graph.core;

import graph.module.DAGModule;

public class ModuleRejectedErrorEdge extends DAGErrorEdge {
	private static final long serialVersionUID = 1L;
	private DAGModule<?> module_;
	private Edge edge_;

	public ModuleRejectedErrorEdge(Edge edge, DAGModule<?> module) {
		module_ = module;
		edge_ = edge;
	}

	@Override
	public String getError() {
		return "Edge " + edge_ + " was rejected by module '" + module_ + "'.";
	}

	@Override
	public Node[] getNodes() {
		return edge_.getNodes();
	}

}
