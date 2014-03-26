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

public class PrimitiveNode implements Node {
	private static final long serialVersionUID = 3362826577155380695L;

	private Object primitive_;

	private PrimitiveNode(boolean value) {
		primitive_ = value;
	}

	private PrimitiveNode(byte value) {
		primitive_ = value;
	}

	private PrimitiveNode(char value) {
		primitive_ = value;
	}

	private PrimitiveNode(double value) {
		primitive_ = value;
	}

	private PrimitiveNode(float value) {
		primitive_ = value;
	}

	private PrimitiveNode(int value) {
		primitive_ = value;
	}

	private PrimitiveNode(long value) {
		primitive_ = value;
	}

	private PrimitiveNode(short value) {
		primitive_ = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimitiveNode other = (PrimitiveNode) obj;
		if (primitive_ == null) {
			if (other.primitive_ != null)
				return false;
		} else if (!primitive_.equals(other.primitive_))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return primitive_.toString();
	}

	public Object getPrimitive() {
		return primitive_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((primitive_ == null) ? 0 : primitive_.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getIdentifier() {
		return "'" + getName();
	}
	
	@Override
	public String getIdentifier(boolean useName) {
		return getIdentifier();
	}

	public static PrimitiveNode parseNode(String name) {
		try {
			if (name.matches("'.'"))
				return new PrimitiveNode(name.charAt(1));
			if (name.equalsIgnoreCase("true"))
				return new PrimitiveNode(true);
			if (name.equalsIgnoreCase("false"))
				return new PrimitiveNode(false);
			if (name.matches("-?\\d{1,4}"))
				return new PrimitiveNode(Short.valueOf(name));
			if (name.matches("-?\\d{5,9}"))
				return new PrimitiveNode(Integer.valueOf(name));
			if (name.matches("-?\\d{10,18}"))
				return new PrimitiveNode(Long.valueOf(name));
			if (name.matches("[-+]?\\d[\\dE+-.]*f?"))
				return new PrimitiveNode(Float.valueOf(name));
			if (name.matches("[-+]?\\d[\\dE+-.]*d?"))
				return new PrimitiveNode(Double.valueOf(name));
			if (name.length() == 1)
				return new PrimitiveNode(name.charAt(0));
		} catch (Exception e) {
		}
		return null;
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		primitive_ = in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(primitive_);
	}
}
