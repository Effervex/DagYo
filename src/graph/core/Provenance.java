/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package graph.core;


import java.util.Date;

/**
 * An abstract class for recording provenance data.
 * 
 * @author Sam Sarjant
 */
public abstract class Provenance {
	/** The creation date. */
	protected Date creation_;

	/** The creator. */
	protected Node creator_;

	/**
	 * Constructor for a new Provenance
	 * 
	 */
	public Provenance() {
		this(null);
	}

	/**
	 * Constructor for a new Provenance
	 * 
	 */
	public Provenance(Node creator) {
		creator_ = creator;
		creation_ = new Date();
	}

	public Date getCreation() {
		return creation_;
	}

	public Node getCreator() {
		return creator_;
	}

	@Override
	public String toString() {
		if (creator_ == null)
			return "Created on " + creation_;
		return "Created by " + creator_ + " on " + creation_;
	}
}
