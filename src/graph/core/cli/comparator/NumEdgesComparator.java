package graph.core.cli.comparator;

import graph.core.Node;
import graph.core.cli.DAGPortHandler;
import graph.module.RelatedEdgeModule;

public class NumEdgesComparator extends DefaultComparator {
	private transient RelatedEdgeModule relEdgeModule_;

	public NumEdgesComparator(DAGPortHandler handler) {
		super(handler);
		relEdgeModule_ = (RelatedEdgeModule) handler.getDAG().getModule(
				RelatedEdgeModule.class);
	}

	@Override
	protected int compareInternal(Object o1, Object o2) {
		if (relEdgeModule_ == null)
			return 0;
		
		// Only works for Nodes
		if (!(o1 instanceof Node))
			if (!(o2 instanceof Node))
				return 0;
			else
				return 1;
		else if (!(o2 instanceof Node))
			return -1;
		int edgeCount1 = relEdgeModule_.execute(o1).size();
		int edgeCount2 = relEdgeModule_.execute(o2).size();
		int result = Integer.compare(edgeCount1, edgeCount2);
		if (result != 0)
			return -result;

		return 0;
	}

}
