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

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;

import java.io.IOException;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import util.serialisation.DefaultSerialisationMechanism;

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
			out.writeInt(dagObj.getID());
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
			int id = in.readInt();

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
