/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sam Sarjant - initial API and implementation
 ******************************************************************************/
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
		filterName_ = subDagFilter;
		handler_ = handler;
	}

	@Override
	public boolean evaluate(Object obj) {
		// Get DAGObject
		DAGObject dagObj = handler_.convertToDAGObject(obj);

		if (dagObj == null)
			return true;

		if (dagObj instanceof DAGNode) {
			if (((DAGNode) dagObj).getProperty(SubDAGExtractorModule.TAG_PREFIX
					+ filterName_) != null
					|| ((DAGNode) dagObj)
							.getProperty(SubDAGExtractorModule.NON_CORE_PREFIX
									+ filterName_) != null)
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
