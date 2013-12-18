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
package graph.edge.properties;

import graph.core.DAGNode;
import graph.core.StringNode;

/**
 * An interface for denoting if an edge is an alias relation. The first argument
 * is the node, and every other argument is a string representing an alias for
 * the node.
 * 
 * @author Sam Sarjant
 */
public interface Alias {
	public StringNode[] getAliases();

	public DAGNode getNode();
}
