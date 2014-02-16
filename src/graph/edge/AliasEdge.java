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
package graph.edge;

import graph.core.DAGNode;
import graph.core.NamedEdge;
import graph.core.StringNode;
import graph.edge.properties.Alias;

/**
 * An edge that explicitly encodes and alias for a node. The arguments are
 * ordered, where the first argument is the node and every following argument is
 * a string.
 * 
 * @author Sam Sarjant
 */
public class AliasEdge extends NamedEdge implements Alias {
	private static final long serialVersionUID = 5626211989049360475L;

	public AliasEdge(DAGNode predicate, StringNode... aliases) {
		super(predicate, aliases);
	}

	@Override
	public DAGNode getNode() {
		return (DAGNode) edgeNodes_[0];
	}

	@Override
	public StringNode[] getAliases() {
		StringNode[] aliases = new StringNode[edgeNodes_.length - 1];
		System.arraycopy(edgeNodes_, 1, aliases, 0, aliases.length);
		return aliases;
	}
}
