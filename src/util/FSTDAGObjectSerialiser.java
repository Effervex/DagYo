/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 ******************************************************************************/
package util;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;

import java.io.IOException;

import util.serialisation.DefaultSerialisationMechanism;
import de.ruedigermoeller.serialization.FSTBasicObjectSerializer;
import de.ruedigermoeller.serialization.FSTClazzInfo;
import de.ruedigermoeller.serialization.FSTClazzInfo.FSTFieldInfo;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

public class FSTDAGObjectSerialiser extends FSTBasicObjectSerializer {
	public static final byte NODES = 2;

	@Override
	public void writeObject(FSTObjectOutput out, Object toWrite,
			FSTClazzInfo clzInfo, FSTFieldInfo referencedBy, int streamPosition)
			throws IOException {
		DAGObject dagObj = (DAGObject) toWrite;
		byte serialisationState = DefaultSerialisationMechanism.idSerialise();
		boolean idOnly = false;
		if (serialisationState == DefaultSerialisationMechanism.ID
				|| (serialisationState == NODES && dagObj instanceof DAGNode))
			idOnly = true;
		if (dagObj.getID() == -1)
			idOnly = false;

		out.writeBoolean(idOnly);
		if (idOnly)
			out.writeFLong(dagObj.getID());
		else
			out.defaultWriteObject(toWrite, clzInfo);
	}

	@Override
	public void readObject(FSTObjectInput in, Object toRead,
			FSTClazzInfo clzInfo, FSTFieldInfo referencedBy)
			throws IOException, ClassNotFoundException, IllegalAccessException,
			InstantiationException {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object instantiate(Class objectClass, FSTObjectInput in,
			FSTClazzInfo serializationInfo, FSTFieldInfo reference,
			int streamPosition) throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		DAGObject obj = null;
		boolean idOnly = in.readBoolean();
		if (idOnly) {
			long id = in.readFLong();

			if (DAGNode.class.isAssignableFrom(objectClass))
				obj = DirectedAcyclicGraph.selfRef_.getNodeByID(id);
			else if (DAGEdge.class.isAssignableFrom(objectClass))
				obj = DirectedAcyclicGraph.selfRef_.getEdgeByID(id);
			else {
				obj = (DAGObject) objectClass.newInstance();
				obj.setID(id);
			}
			in.registerObject(obj, streamPosition, serializationInfo, reference);
			return obj;
		} else {
			obj = (DAGObject) objectClass.newInstance();
			in.defaultReadObject(reference, serializationInfo, obj);
			in.registerObject(obj, streamPosition, serializationInfo, reference);
		}
		return obj;
	}
}
