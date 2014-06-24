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

/**
 * The different formats the DAG can be exported to text as.
 * 
 * @author Sam Sarjant
 */
public enum DAGExportFormat {
	DAG_COMMANDS, CSV_TAXONOMIC, CSV_ALL, EDGES;
}
