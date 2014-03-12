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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.ruedigermoeller.serialization.annotations.Compress;

import util.UtilityMethods;

public class StringNode implements Node {
	private static final long serialVersionUID = 2912475401266723942L;
	@Compress
	private String str_;

	public StringNode(String string) {
		while (string.startsWith("\"") && string.endsWith("\""))
			string = UtilityMethods.shrinkString(string, 1);
		str_ = string;
		str_ = str_.replaceAll("(?<!\\\\)\"", "\\\"");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringNode other = (StringNode) obj;
		if (str_ == null) {
			if (other.str_ != null)
				return false;
		} else if (!str_.equals(other.str_))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return str_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((str_ == null) ? 0 : str_.hashCode());
		return result;
	}

	@Override
	public String toString() {
		// TODO This is not coming up as escaped.
		return "\"" + getName() + "\"";
	}

	@Override
	public String getIdentifier() {
		return toString();
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		str_ = (String) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(str_);
	}
}
