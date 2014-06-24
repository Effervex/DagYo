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
package util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A convenience class for storing default boolean flags.
 * 
 * @author Sam Sarjant
 */
public class BooleanFlags {
	private boolean[] vals_ = new boolean[0];
	private Map<String, Integer> flagPos_ = new HashMap<>();

	public void addFlag(String name, boolean defaultVal) {
		int oldLength = vals_.length;
		boolean[] larger = Arrays.copyOf(vals_, oldLength + 1);
		larger[oldLength] = defaultVal;
		vals_ = larger;
		flagPos_.put(name, oldLength);
	}

	/**
	 * Gets the boolean value for a given flag, allowing this to be read from an
	 * optional flag array.
	 * 
	 * @param name
	 *            The name of the flag to get.
	 * @return
	 */
	public boolean getFlag(String name) {
		Integer i = flagPos_.get(name);
		if (i == null || i >= vals_.length)
			return false;
		return vals_[i];
	}

	/**
	 * Creates a new BooleanFlag object with the flag values loaded into it.
	 * 
	 * @param flags
	 *            The flags to set for the new object.
	 * @return The copied {@link BooleanFlags} object, with values set to flags.
	 */
	public BooleanFlags loadFlags(boolean[] flags) {
		BooleanFlags newBFlags = new BooleanFlags();
		newBFlags.flagPos_ = flagPos_;
		newBFlags.vals_ = Arrays.copyOf(vals_, vals_.length);
		if (flags != null)
			for (int i = 0; i < flags.length; i++)
				newBFlags.vals_[i] = flags[i];
		return newBFlags;
	}

	public String flagString() {
		StringBuilder buffer = new StringBuilder();
		SortedSet<String> flags = new TreeSet<>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Integer.compare(flagPos_.get(o1), flagPos_.get(o2));
			}
		});
		flags.addAll(flagPos_.keySet());
		for (String flag : flags) {
			if (buffer.length() > 0)
				buffer.append(", ");
			buffer.append(flag + " (" + vals_[flagPos_.get(flag)] + ")");
		}
		return buffer.toString();
	}
}
