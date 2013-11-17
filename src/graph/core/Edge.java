package graph.core;

import java.io.Externalizable;
import java.io.Serializable;

public interface Edge extends Serializable, Identifiable, UniqueID {
	public boolean containsNode(Node node);

	public Node[] getNodes();
	
	public String toString(boolean useIDs);
}