/*******************************************************************************
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sam Sarjant - initial API and implementation
 ******************************************************************************/
package graph.module;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import graph.core.StringNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The string storage module is a memory-conservation module for moving the
 * storage of specific StringNode edges to a read-write file. The result is that
 * unnecessary strings are not taking up space in memory at the cost of a slower
 * access speed.
 * 
 * @author Sam Sarjant
 */
public class StringStorageModule extends DAGModule<Boolean> {
	private static final long serialVersionUID = 1L;

	private static final int COMPRESSION_THRESHOLD = 8;

	private static final int MAX_FILE_SIZE = (int) Math.pow(2, 27);

	public static final String FILE_LOC = DirectedAcyclicGraph.selfRef_.rootDir_
			+ "/strings.dat";

	/** The set of predicates to be compressed. */
	private Set<String> registeredPreds_;

	private transient BufferedWriter out_;

	private int outSize_;

	private int fileNum_;

	/**
	 * Compresses an edge by replacing all StringNode arguments with pointers to
	 * specific file locations containing the original string.
	 * 
	 * @param edge
	 *            The edge to compress.
	 * @return The compressed edge, or the original if it is already fully
	 *         compressed (or cannot be compressed).
	 * @throws IOException
	 *             Should something go awry.
	 */
	private DAGEdge compressEdge(DAGEdge edge) throws IOException {
		if (!isCompressedEdge(edge.getNodes()[0]))
			return edge;

		// Get the edge nodes and creator
		Node[] edgeNodes = edge.getNodes();
		Node creator = null;

		// Compress the arguments whenever there is a string
		Node[] compressedArgs = new Node[edgeNodes.length];
		boolean changed = false;
		for (int i = 0; i < edgeNodes.length; i++) {
			if (edgeNodes[i] instanceof StringNode
					&& edgeNodes[i].getName().length() >= COMPRESSION_THRESHOLD) {
				CompressedStringNode compressedNode = writeString(edgeNodes[i]
						.getName());
				compressedArgs[i] = compressedNode;
				changed = true;
			} else
				compressedArgs[i] = edgeNodes[i];
		}

		if (changed) {
			// Initialise the creator
			if (edge.getCreator() != null && creator == null)
				creator = dag_.findOrCreateNode(edge.getCreator(), null, false);
			return (DAGEdge) dag_.findOrCreateEdge(compressedArgs, creator,
					true);
		}
		return edge;
	}

	/**
	 * Writes a string to file to be compressed and returns the pointer string
	 * to it.
	 * 
	 * @param str
	 *            The string to be written to file.
	 * @return The pointer string.
	 * @throws IOException
	 *             Should something go awry...
	 */
	private CompressedStringNode writeString(String str) throws IOException {
		if (out_ == null) {
			// Set up the file (append to existing or start anew)
			fileNum_ = 0;
			while (new File(FILE_LOC + fileNum_).exists())
				fileNum_++;
			// TODO Complete this
			out_ = new BufferedWriter(new FileWriter(FILE_LOC + fileNum_));
			outSize_ = 0;
		} else if (outSize_ >= MAX_FILE_SIZE) {
			// File is full - start a new one.
			out_.close();
			fileNum_++;
			out_ = new BufferedWriter(new FileWriter(FILE_LOC + fileNum_));
			outSize_ = 0;
		}

		// Write out a compressed node recording the details.
		CompressedStringNode compNode = new CompressedStringNode(fileNum_,
				outSize_);
		out_.write(str + "\n");
		outSize_ += str.length();
		return compNode;
	}

	/**
	 * If the edge is compressable based on the predicate used.
	 * 
	 * @param edge
	 *            The edge to compress/decompress.
	 * @return True if the edge predicate is registered with this module.
	 */
	protected boolean isCompressedEdge(Node predicate) {
		if (registeredPreds_ == null || registeredPreds_.isEmpty())
			return false;
		return registeredPreds_.contains(predicate.getName());
	}

	@Override
	public boolean addEdge(DAGEdge edge) {
		try {
			DAGEdge compressedEdge = compressEdge(edge);
			if (compressedEdge != edge)
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.addEdge(edge);
	}

	@Override
	public Boolean execute(Object... args) throws IllegalArgumentException,
			ModuleException {
		// No op.
		return false;
	}

	@Override
	public boolean initialisationComplete(Collection<DAGNode> nodes,
			Collection<DAGEdge> edges, boolean forceRebuild) {
		// Initialise the I/O locations
		return super.initialisationComplete(nodes, edges, forceRebuild);
	}

	/**
	 * Registers a predicate name to compress.
	 * 
	 * @param predicateName
	 *            The name of the predicate to compress string arguments for.
	 */
	public void registerCompressableNode(String predicateName) {
		if (registeredPreds_ == null)
			registeredPreds_ = new HashSet<>();
		registeredPreds_.add(predicateName);
	}

	public void flush() {
		try {
			out_.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
