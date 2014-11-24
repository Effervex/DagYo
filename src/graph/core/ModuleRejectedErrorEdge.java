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
package graph.core;

import graph.module.DAGModule;

public class ModuleRejectedErrorEdge extends DAGErrorEdge {
	private static final long serialVersionUID = 1L;
	private DAGModule<?> module_;
	private DAGEdge edge_;

	public ModuleRejectedErrorEdge(DAGEdge edge, DAGModule<?> module) {
		module_ = module;
		edge_ = edge;
	}

	@Override
	public String getError(boolean isPretty) {
		return "Edge " + edge_.toString(!isPretty)
				+ " was rejected by module '" + module_ + "'.";
	}

	@Override
	public Node[] getNodes() {
		return edge_.getNodes();
	}

}
