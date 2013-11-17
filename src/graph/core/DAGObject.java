package graph.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.DefaultSerialisationMechanism;

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
		properties_.put(CREATION_DATE, new Date().getTime() + "");
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

//	@Override
	public final void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// Read serialisation format
		boolean minSerial = in.readBoolean();
		id_ = in.readLong();
		if (!minSerial) {
			int numProps = in.readInt();
			for (int i = 0; i < numProps; i++) {
				properties_.put((String) in.readObject(),
						(String) in.readObject());
			}
			readFullObject(in);
		}
	}

	@Override
	public String toString() {
		if (!properties_.containsKey(CREATOR))
			return "Created on " + getCreationDate();
		return "Created by " + getCreator() + " on " + getCreationDate();
	}

//	@Override
	public final void writeExternal(ObjectOutput out) throws IOException {
		// Write externalizable format
		boolean idSerialisation = DefaultSerialisationMechanism.idSerialise();
		out.writeBoolean(idSerialisation);
		out.writeLong(id_);
		if (!idSerialisation) {
			out.writeInt(properties_.size());
			for (String key : properties_.keySet()) {
				out.writeObject(key);
				out.writeObject(properties_.get(key));
			}
			writeFullObject(out);
		}
	}
}
