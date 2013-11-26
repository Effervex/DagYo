package graph.core;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.UniqueID;
import util.serialisation.DefaultSerialisationMechanism;

/**
 * A generic DAG object. This object has an ID and properties.
 * 
 * ==== IMPORTANT ==== Ensure that any subclass defines a default constructor. I
 * don't know how to enforce this, but it is required for module serialisation.
 * 
 * @author Sam Sarjant
 */
// @EqualnessIsBinary
public abstract class DAGObject implements UniqueID, Serializable,
		Identifiable, Comparable<DAGObject> {
	private static final long serialVersionUID = -5088948795943227278L;
	public static final String CREATION_DATE = "creationDate";
	public static final String CREATOR = "creator";

	private Map<String, String> properties_;

	protected long id_;

	public DAGObject() {
		this(null);
	}

	public DAGObject(Node creator) {
		properties_ = new HashMap<>();
		if (creator != null)
			properties_.put(CREATOR, creator.getIdentifier());
		properties_.put(CREATION_DATE, System.currentTimeMillis() + "");
	}

	protected abstract void readFullObject(ObjectInput in) throws IOException,
			ClassNotFoundException;

	protected abstract void writeFullObject(ObjectOutput out)
			throws IOException;

	void put(String key, String value) {
		properties_.put(key, value);
	}

	void remove(String key) {
		if (properties_ != null)
			properties_.remove(key);
	}

	@Override
	public int compareTo(DAGObject o) {
		if (o == null)
			return -1;
		return Long.compare(getID(), o.getID());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DAGNode other = (DAGNode) obj;
		if (getID() != other.getID())
			return false;
		return true;
	}

	public Date getCreationDate() {
		return new Date(Long.parseLong(properties_.get(CREATION_DATE)));
	}

	public String getCreator() {
		return properties_.get(CREATOR);
	}

	@Override
	public String getIdentifier() {
		return "" + id_;
	}

	public Map<String, String> getProperties() {
		return properties_;
	}

	public String getProperty(String key) {
		return properties_.get(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long id = getID();
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	// @Override
	@SuppressWarnings("unchecked")
	public final void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// Read serialisation format
		boolean minSerial = in.readBoolean();
		id_ = in.readLong();
		if (!minSerial) {
			properties_ = (Map<String, String>) in.readObject();
			readFullObject(in);
		}
	}

	@Override
	public String toString() {
		return id_ + "";
	}

	// @Override
	public final void writeExternal(ObjectOutput out) throws IOException {
		// Write externalizable format
		boolean idSerialisation = DefaultSerialisationMechanism.idSerialise();
		out.writeBoolean(idSerialisation);
		out.writeLong(id_);
		if (!idSerialisation) {
			out.writeObject(properties_);
			writeFullObject(out);
		}
	}

	/**
	 * Only use this method if you're absolutely sure of the object's ID, and
	 * the object represents a skeleton reference.
	 * 
	 * @param id
	 *            The ID to set.
	 */
	public void setID(long id) {
		id_ = id;
	}

	@Override
	public long getID() {
		return id_;
	}
}
