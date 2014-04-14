package graph.core.cli.filters;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.Node;
import graph.core.cli.DAGPortHandler;
import graph.module.SubDAGExtractorModule;

import org.apache.commons.collections4.Predicate;

public class SubDAGFilter implements Predicate<Object> {
	private String filterName_;
	private DAGPortHandler handler_;

	public SubDAGFilter(String subDagFilter, DAGPortHandler handler) {
		filterName_ = SubDAGExtractorModule.TAG_PREFIX + subDagFilter;
		handler_ = handler;
	}

	@Override
	public boolean evaluate(Object obj) {
		// Get DAGObject
		DAGObject dagObj = handler_.convertToDAGObject(obj);
		
		if (dagObj == null)
			return true;
		
		if (dagObj instanceof DAGNode) {
			if (((DAGNode) dagObj).getProperty(filterName_) != null)
				return true;
			else
				return false;
		}
		if (dagObj instanceof DAGEdge) {
			Node[] nodes = ((DAGEdge) dagObj).getNodes();
			for (Node node : nodes)
				if (!evaluate(node))
					return false;
			return true;
		}

		// Otherwise, object is fine
		return true;
	}

}
