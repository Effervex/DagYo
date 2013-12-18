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
package graph.core;

public class NonExistentNodeException extends DAGException {

	private static final long serialVersionUID = -6665757721192481789L;

	public NonExistentNodeException(Node n, Node[] edgeNodes) {
		super("Node " + n + " has not yet been created for edge: " + edgeNodes);
	}

}
