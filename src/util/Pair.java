/*
 *    This file is part of the CERRLA algorithm
 *
 *    CERRLA is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    CERRLA is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with CERRLA. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    src/util/Pair.java
 *    Copyright (C) 2012 Samuel Sarjant
 */
package util;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	private static final long serialVersionUID = 1535407435275755417L;
	public A objA_;
	public B objB_;

	public Pair(A a, B b) {
		objA_ = a;
		objB_ = b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objA_ == null) ? 0 : objA_.hashCode());
		result = prime * result + ((objB_ == null) ? 0 : objB_.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (objA_ == null) {
			if (other.objA_ != null)
				return false;
		} else if (!objA_.equals(other.objA_))
			return false;
		if (objB_ == null) {
			if (other.objB_ != null)
				return false;
		} else if (!objB_.equals(other.objB_))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return objA_ + ", " + objB_;
	}
}
