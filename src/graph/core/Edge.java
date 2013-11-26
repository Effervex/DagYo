package graph.core;

import java.io.Serializable;

import util.UniqueID;

public interface Edge extends Serializable, Identifiable, UniqueID {
	public boolean containsNode(Node node);

	public Node[] getNodes();
	
	public String toString(boolean useIDs);
}