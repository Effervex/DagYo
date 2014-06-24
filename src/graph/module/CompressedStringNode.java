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
package graph.module;

import graph.core.DirectedAcyclicGraph;
import graph.core.Node;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * A node representing compressed data. When accessed, it reads data from a
 * file.
 * 
 * @author Sam Sarjant
 */
public class CompressedStringNode implements Node {
	private static final long serialVersionUID = 1L;

	/** The current file this data is stored in. */
	private int file_;

	/** The start of the stored string data. */
	private int pointerStart_;

	public CompressedStringNode(int file, int strStart) {
		file_ = file;
		pointerStart_ = strStart;
	}

	@Override
	public String getIdentifier() {
		return toString();
	}

	@Override
	public String getIdentifier(boolean useName) {
		return getIdentifier();
	}

	@Override
	public String getName() {
		try {
			// Flush the writer
			((StringStorageModule) DirectedAcyclicGraph.selfRef_
					.getModule(StringStorageModule.class)).flush();

			BufferedReader in = new BufferedReader(new FileReader(
					StringStorageModule.FILE_LOC + file_));
			in.skip(pointerStart_);

			String result = in.readLine();
			in.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "\"" + getName() + "\"";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + file_;
		result = prime * result + pointerStart_;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompressedStringNode other = (CompressedStringNode) obj;
		if (file_ != other.file_)
			return false;
		if (pointerStart_ != other.pointerStart_)
			return false;
		return true;
	}
}
