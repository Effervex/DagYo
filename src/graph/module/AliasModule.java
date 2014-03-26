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
package graph.module;

import graph.core.DAGNode;

import java.util.Collection;

import util.AliasedObject;

public interface AliasModule {
	/**
	 * Execute the alias module and return all DAGNodes fitting the alias.
	 * 
	 * @param alias
	 *            The alias to search with.
	 * @param caseSensitive
	 *            If the alias is case sensitive.
	 * @param exactString
	 *            If the alias must be an exact string.
	 * @return All DAGNodes with the given alias.
	 */
	public Collection<DAGNode> findNodes(String alias, boolean caseSensitive,
			boolean exactString);

	/**
	 * Execute the alias module and return all DAGNodes fitting the alias, with
	 * the matching alias included in the results.
	 * 
	 * @param alias
	 *            The alias to search with.
	 * @param caseSensitive
	 *            If the alias is case sensitive.
	 * @param exactString
	 *            If the alias must be an exact string.
	 * @return All DAGNodes with the given alias and the alias for each node.
	 */
	public Collection<AliasedObject<Character, DAGNode>> findAliasedNodes(
			String alias, boolean caseSensitive, boolean exactString);
}
