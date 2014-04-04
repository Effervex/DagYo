package graph.core.cli.filters;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.Node;
import graph.module.SubDAGExtractorModule;

import org.apache.commons.collections4.Predicate;

public class SubDAGFilter implements Predicate<Object> {
	private String filterName_;

	public SubDAGFilter(String subDagFilter) {
		filterName_ = SubDAGExtractorModule.TAG_PREFIX + subDagFilter;
	}

	@Override
	public boolean evaluate(Object obj) {
		if (obj == null)
			return false;
		
		if (obj instanceof DAGNode) {
			if (((DAGNode) obj).getProperty(filterName_) != null)
				return true;
			else
				return false;
		}
		if (obj instanceof DAGEdge) {
			Node[] nodes = ((DAGEdge) obj).getNodes();
			for (Node node : nodes)
				if (!evaluate(node))
					return false;
			return true;
		}
		
		// Otherwise, object is fine
		return true;
	}

}
