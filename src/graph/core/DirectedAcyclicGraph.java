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
package graph.core;

import graph.module.DAGModule;
import graph.module.NodeAliasModule;
import graph.module.RelatedEdgeModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import util.BooleanFlags;
import util.FSTDAGObjectSerialiser;
import util.UtilityMethods;
import util.collection.HashIndexedCollection;
import util.collection.IndexedCollection;
import util.serialisation.DefaultSerialisationMechanism;
import util.serialisation.FSTSerialisationMechanism;
import util.serialisation.SerialisationMechanism;

/**
 * The class representing the access point of the directed acyclic graph.
 * 
 * @author Sam Sarjant
 */
public class DirectedAcyclicGraph {
	private static final String DAG_FILE = "commandLog.log";

	private static final String EDGE_FILE = "edges.dat";

	private static final String EDGE_ID_FIELD = "edgeID";

	private static final String EDGE_TXT_FILE = "edges.txt";

	private static final int MAX_OBJ_SERIALISATION = 5000000;

	private static final String NODE_FILE = "nodes.dat";

	private static final String NODE_ID_FIELD = "nodeID";

	private static final String NODE_TXT_FILE = "nodes.txt";

	private static final String NUM_EDGES_FIELD = "numEdges";

	private static final String NUM_NODES_FIELD = "numNodes";

	public static final File DEFAULT_ROOT = new File("dag");

	public static final BooleanFlags edgeFlags_;

	public static final String EPHEMERAL_MARK = "ephem";

	public static final String GLOBALS_FILE = "dagDetails";

	public static final File MODULE_FILE = new File("activeModules.config");

	public static final BooleanFlags nodeFlags_;

	public static final Pattern PRIMITIVE_PATTERN = Pattern
			.compile("((?:-?[\\d][\\d.E]*)|\\S)\\)*");

	public static DirectedAcyclicGraph selfRef_;

	private boolean changedState_ = false;

	private BufferedWriter dagOut_;

	private Map<String, Integer> moduleMap_;

	private ArrayList<DAGModule<?>> modules_;

	protected File edgeFile_;

	protected final Lock edgeLock_;

	protected IndexedCollection<DAGEdge> edges_;

	protected File nodeFile_;

	protected final Lock nodeLock_;

	protected IndexedCollection<DAGNode> nodes_;

	protected final Random random_;

	public boolean noChecks_ = false;

	public File rootDir_;

	public final long startTime_;

	public DirectedAcyclicGraph() {
		this(DEFAULT_ROOT, null, null);
	}

	@SuppressWarnings("unchecked")
	public DirectedAcyclicGraph(File rootDir, File nodeFile, File edgeFile) {
		startTime_ = System.currentTimeMillis();
		System.out.print("Initialising... ");

		FSTSerialisationMechanism.conf.get().registerSerializer(
				DAGObject.class, new FSTDAGObjectSerialiser(), true);
		selfRef_ = this;

		try {
			int i = 0;
			File dagFile = new File(DAG_FILE);
			while (dagFile.exists())
				dagFile = new File(DAG_FILE + i++);
			dagOut_ = new BufferedWriter(new FileWriter(dagFile));
		} catch (IOException e) {
			System.err.println("Problem creating command out file.");
			e.printStackTrace();
		}

		random_ = new Random();
		nodes_ = (IndexedCollection<DAGNode>) readDAGFile(rootDir, NODE_FILE);
		nodeLock_ = new ReentrantLock();
		edges_ = (IndexedCollection<DAGEdge>) readDAGFile(rootDir, EDGE_FILE);
		edgeLock_ = new ReentrantLock();
		nodeFile_ = nodeFile;
		edgeFile_ = edgeFile;

		// Load the modules in
		modules_ = new ArrayList<>();
		moduleMap_ = new HashMap<>();
		rootDir_ = rootDir;
		readModules(rootDir);

		System.out.println("Done!");
	}

	private Collection<String> compilePertinentProperties() {
		Collection<String> props = new ArrayList<>();
		for (DAGModule<?> module : modules_) {
			props.addAll(module.getPertinentProperties());
			module.disableCached();
		}
		return props;
	}

	private void readDAGDetails(File rootDir) {
		File details = new File(rootDir, GLOBALS_FILE);
		try {
			if (details.exists()) {
				BufferedReader in = new BufferedReader(new FileReader(details));
				String input = null;
				while ((input = in.readLine()) != null) {
					if (!input.startsWith("%")) {
						String[] split = input.split("=");
						if (split[0].equals(NUM_NODES_FIELD))
							nodes_.setSize(Integer.parseInt(split[1]));
						else if (split[0].equals(NODE_ID_FIELD))
							DAGNode.idCounter_ = Integer.parseInt(split[1]);
						else if (split[0].equals(NUM_EDGES_FIELD))
							edges_.setSize(Integer.parseInt(split[1]));
						else if (split[0].equals(EDGE_ID_FIELD))
							DAGEdge.idCounter_ = Integer.parseInt(split[1]);
					}
				}
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private IndexedCollection<? extends DAGObject> readDAGFile(File rootDir,
			String collectionFile) {
		IndexedCollection<DAGObject> indexedCollection = null;
		try {
			File serFile = new File(rootDir, collectionFile);
			if (serFile.exists()) {
				indexedCollection = new HashIndexedCollection<DAGObject>(
						MAX_OBJ_SERIALISATION);
				// Read it in
				System.out.println("Loading " + collectionFile + "...");
				Object deserialised = SerialisationMechanism.FST
						.getSerialiser().deserialize(serFile);
				if (deserialised instanceof DAGObject[]) {
					DAGObject[] array = (DAGObject[]) deserialised;
					for (DAGObject obj : array)
						indexedCollection.add(obj);
				} else
					indexedCollection = (IndexedCollection<DAGObject>) deserialised;
			} else {
				serFile = new File(rootDir, collectionFile + "0");
				if (serFile.exists()) {
					// Find number of files
					int n = 1;
					while (new File(rootDir, collectionFile + n).exists())
						n++;
					indexedCollection = new HashIndexedCollection<DAGObject>(
							MAX_OBJ_SERIALISATION * n);
					// Load and deserialise the files
					System.out.println("Loading the " + n + " split "
							+ collectionFile + "s...");
					for (int i = 0; i < n; i++) {
						serFile = new File(rootDir, collectionFile + i);
						DAGObject[] array = (DAGObject[]) SerialisationMechanism.FST
								.getSerialiser().deserialize(serFile);
						for (DAGObject obj : array)
							indexedCollection.add(obj);
						System.out.println(collectionFile + i + " complete.");
					}
				} else
					// TODO Might be able to squeeze memory here by swapping
					// hashmap for array
					indexedCollection = new HashIndexedCollection<DAGObject>(
							MAX_OBJ_SERIALISATION);
			}
		} catch (Exception e) {
			System.err.println("Exception while deserialising '"
					+ collectionFile + "'. Creating new collection.");
		}
		return indexedCollection;
	}

	private void readModules(File rootDir) {
		Collection<String> modules = new ArrayList<>();
		try {
			if (!MODULE_FILE.exists()) {
				MODULE_FILE.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(
						MODULE_FILE));
				out.write("% Put the utilised modules here. One per line, in the form <classpath>\n");
				out.write("% E.g.:\n");
				out.write("% graph.module.RelatedEdgeModule");
				out.close();
				return;
			}

			BufferedReader reader = new BufferedReader(new FileReader(
					MODULE_FILE));
			String input = null;
			while ((input = reader.readLine()) != null) {
				if (input.startsWith("%"))
					continue;
				modules.add(input);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String module : modules) {
			System.out.println("Loading " + module + " module...");
			try {
				DAGModule<?> dagModule = DAGModule.loadCreateModule(rootDir_,
						Class.forName(module));
				addModule(dagModule);
			} catch (Exception e) {
				System.err.println("Error loading " + module);
				e.printStackTrace();
			}
		}
		for (DAGModule<?> module : modules_)
			module.setDAG(this);
	}

	/**
	 * Reads a plain text file of nodes/edges and creates new nodes/edges for
	 * every item (including triggering module-based operations).
	 * 
	 * @param filename
	 *            The file being read in.
	 * @param isEdge
	 *            If reading edges (otherwise nodes).
	 * @throws IOException
	 */
	private void readPlainTextFile(File filename, boolean isEdge)
			throws IOException {
		System.out.println("Reading in " + ((isEdge) ? "edges" : "nodes")
				+ "...");
		// Total lines
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		lnr.skip(Long.MAX_VALUE);
		int numLines = lnr.getLineNumber();
		float onePercent = numLines * 0.01f;
		int percent = 1;
		lnr.close();

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String columnProps = reader.readLine();
		String[] props = columnProps.split("\\t");
		int lineNum = 1;
		// Read every line
		String input = null;
		while ((input = reader.readLine()) != null) {
			try {
				lineNum++;
				String[] split = UtilityMethods.splitToArray(input, '\t');
				if (split.length <= 1) {
					System.err.println("Error splitting input (" + lineNum
							+ ") " + input);
					continue;
				}

				DAGObject dagObj = parseDAGObject(split[0], isEdge);
				if (dagObj == null) {
					System.err.println("(" + lineNum + ") " + input);
					continue;
				}

				// Adding props
				for (int i = 1; i < split.length; i++) {
					if (!split[i].isEmpty()) {
						addProperty(dagObj, props[i], split[i]);
					}
				}

				// Status update
				if (lineNum >= percent * onePercent) {
					if ((percent % 10) == 0)
						System.out.print(percent + "%");
					else
						System.out.print(".");
					percent++;
				}
			} catch (Exception e) {
				System.err.println(lineNum + "(ERROR): " + input);
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.out.println();
		reader.close();
	}

	private void saveDAGFile(IndexedCollection<? extends DAGObject> collection,
			File rootDir, String collectionFile, int maxNumObjects)
			throws IOException {
		byte serialisationType = (collectionFile.equals(EDGE_FILE)) ? FSTDAGObjectSerialiser.NODES
				: DefaultSerialisationMechanism.NORMAL;

		// Splitting the file if necessary
		int size = collection.size();
		boolean isSplitting = size > maxNumObjects;
		DAGObject[] array = collection.toArray(new DAGObject[size]);
		for (int i = 0; i < size; i += maxNumObjects) {
			DAGObject[] subarray = null;
			File serFile = null;
			if (isSplitting) {
				int arraySize = Math.min(maxNumObjects, size - i);
				subarray = new DAGObject[arraySize];
				System.arraycopy(array, i, subarray, 0, arraySize);
				serFile = new File(rootDir, collectionFile
						+ (i / maxNumObjects));
			} else {
				subarray = array;
				serFile = new File(rootDir, collectionFile);
			}

			// Write the subarray
			serFile.getParentFile().mkdirs();
			serFile.createNewFile();
			try {
				SerialisationMechanism.FST.getSerialiser().serialize(subarray,
						serFile, serialisationType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (isSplitting)
			new File(rootDir, collectionFile).delete();
	}

	/**
	 * Saves the DAG data to a human-readable and re-parsable file.
	 * 
	 * @param collection
	 *            The collection of DAG objects to save.
	 * @param rootDir
	 *            The root directory to save into.
	 * @param collectionFile
	 *            The file to save under.
	 * @param maxNumObjects
	 *            The maximum number of objects per file.
	 * @throws IOException
	 *             Should something go awry...
	 */
	private void savePlainTextDAGFile(
			IndexedCollection<? extends DAGObject> collection, File rootDir,
			String collectionFile, int maxNumObjects) throws IOException {
		// Objects used throughout per file.
		ArrayList<String> propIndex = null;
		File txtFile = null;

		// Write the subarray
		txtFile = new File(rootDir, collectionFile);
		txtFile.getParentFile().mkdirs();
		txtFile.createNewFile();

		// Write the object to file with props
		propIndex = new ArrayList<>();
		propIndex.add("DAGObject");

		// For every item
		BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile));
		for (DAGObject dagObj : collection) {
			StringBuilder builder = new StringBuilder(
					dagObj.getIdentifier(true));
			int propCount = 0;
			for (int j = 1; j < propIndex.size(); j++) {
				String val = dagObj.getProperty(propIndex.get(j));
				builder.append("\t");
				if (val != null) {
					builder.append(val);
					propCount++;
				}
			}

			// Add unknown props
			String[] props = dagObj.getProperties();
			if (propCount < props.length / 2) {
				for (int j = 0; j < props.length; j += 2) {
					if (!propIndex.contains(props[j])) {
						propIndex.add(props[j]);
						builder.append("\t" + props[j + 1]);
					}
				}
			}
			writer.write(builder + "\n");
		}
		writer.close();

		// Write props at top of file
		File tempFile = new File("temp.txt");
		BufferedWriter tempOut = new BufferedWriter(new FileWriter(tempFile));
		BufferedReader reader = new BufferedReader(new FileReader(txtFile));
		tempOut.write(StringUtils.join(propIndex, '\t') + "\n");

		String input = null;
		while ((input = reader.readLine()) != null)
			tempOut.write(input + "\n");
		reader.close();
		tempOut.close();
		txtFile.delete();
		tempFile.renameTo(txtFile);
	}

	private void writeDAGDetails(BufferedWriter out) throws IOException {
		out.write("% Do not change the contents of this file "
				+ "unless you know what you're doing!\n");
		out.write(NUM_NODES_FIELD + "=" + nodes_.size() + "\n");
		out.write(NODE_ID_FIELD + "=" + DAGNode.idCounter_ + "\n");
		out.write(NUM_EDGES_FIELD + "=" + edges_.size() + "\n");
		out.write(EDGE_ID_FIELD + "=" + DAGEdge.idCounter_ + "\n");
	}

	protected synchronized void addModule(DAGModule<?> module) {
		modules_.add(module);
		moduleMap_.put(module.getClass().getCanonicalName(),
				modules_.indexOf(module));
	}

	protected void exportAsEdges(BufferedWriter out) throws IOException {
		for (DAGEdge e : edges_)
			out.write(e.getIdentifier(true) + "\n");
	}

	protected void exportToCSV(BufferedWriter out, DAGExportFormat format)
			throws IOException {
		for (DAGEdge e : edges_) {
			Node[] nodes = e.getNodes();
			for (Node n : nodes) {
				String name = n.getIdentifier(true);
				out.write(name + ",");
			}
			out.write("\n");
		}
	}

	protected void exportToDAG(BufferedWriter out) throws IOException {
		Collection<String> pertinentProperties = compilePertinentProperties();

		for (DAGNode n : nodes_) {
			out.write("$0$=addnode " + n.getIdentifier(true) + "\n");
			String[] props = n.getProperties();
			for (int i = 0; i < props.length; i += 2) {
				if (!pertinentProperties.contains(props[i]))
					out.write("addprop N $0$ \"" + props[i] + "\" |\\n"
							+ props[i + 1] + "\\n|\n");
			}
		}
		for (DAGEdge e : edges_) {
			out.write("$0$=addedge " + e.getIdentifier(true) + "\n");
			String[] props = e.getProperties();
			for (int i = 0; i < props.length; i += 2) {
				if (!pertinentProperties.contains(props[i]))
					out.write("addprop E $0$ \"" + props[i] + "\" |\\n"
							+ props[i + 1] + "\\n|\n");
			}
		}
	}

	protected void initialiseInternal() {
		readDAGDetails(rootDir_);
		if (nodeFile_ != null) {
			// Read in node/edge file
			try {
				if (nodeFile_ != null)
					readPlainTextFile(nodeFile_, false);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (edgeFile_ != null) {
			// Read in node/edge file
			try {
				if (edgeFile_ != null)
					readPlainTextFile(edgeFile_, true);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	protected SortedSet<DAGEdge> orderedReassertables() {
		return new TreeSet<DAGEdge>();
	}

	protected String preParseNode(String nodeStr, Node creator,
			boolean createNew, boolean dagNodeOnly) {
		return nodeStr;
	}

	public synchronized void addProperty(DAGObject dagObj, String key,
			String value) {
		changedState_ = true;
		dagObj.put(key, value);
		if (dagObj instanceof DAGNode)
			nodes_.update((DAGNode) dagObj);
		else if (dagObj instanceof DAGEdge)
			edges_.update((DAGEdge) dagObj);

		for (DAGModule<?> module : modules_)
			module.addProperty(dagObj, key, value);
	}

	public void clear() {
		changedState_ = true;
		nodes_.clear();
		edges_.clear();

		// Trigger modules
		for (DAGModule<?> module : modules_)
			module.clear();
	}

	public void export(File file, DAGExportFormat format) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		switch (format) {
		case DAG_COMMANDS:
			exportToDAG(out);
			break;
		case CSV_ALL:
		case CSV_TAXONOMIC:
			exportToCSV(out, format);
			break;
		case EDGES:
			exportAsEdges(out);
			break;
		}

		out.close();
	}

	public DAGNode findDAGNode(String nodeName) {
		NodeAliasModule nodeAlias = (NodeAliasModule) getModule(NodeAliasModule.class);
		Collection<DAGNode> nodes = nodeAlias.findNodeByName(nodeName, true);
		if (nodes.isEmpty())
			return null;
		if (nodes.size() > 1)
			System.err.println("WARNING: More than one node found with name: "
					+ nodeName);
		return nodes.iterator().next();
	}

	/**
	 * Finds an existing edge with the given nodes.
	 * 
	 * @param edgeNodes
	 *            The nodes of the edge.
	 * @return The found edge or null if it does not exist.
	 */
	public Edge findEdge(Node... edgeNodes) {
		RelatedEdgeModule relMod = (RelatedEdgeModule) getModule(RelatedEdgeModule.class);
		Collection<Edge> edges = relMod.findEdgeByNodes(edgeNodes);
		if (edges.isEmpty())
			return null;
		if (edges.size() > 1)
			System.err.println("WARNING: More than one edge found with nodes: "
					+ Arrays.toString(edgeNodes));
		return edges.iterator().next();
	}

	/**
	 * Finds or creates an edge from a set of nodes. The returned edge either
	 * already exists, or is newly created and added.
	 * 
	 * @param edgeNodes
	 *            The nodes of the edge.
	 * @param creator
	 *            The creator of the edge (can be null).
	 * @param flags
	 *            The boolean flags to use during edge creation: createNew
	 *            (false), ephemeral (false).
	 * @return The created edge or an ErrorEdge if there was an error.
	 */
	public synchronized Edge findOrCreateEdge(Node[] edgeNodes, Node creator,
			boolean... flags) {
		BooleanFlags bFlags = edgeFlags_.loadFlags(flags);
		boolean createNew = bFlags.getFlag("createNew");
		edgeLock_.lock();
		try {
			Edge edge = findEdge(edgeNodes);
			if (edge == null && createNew) {
				// Check all the nodes are in the DAG
				if (!noChecks_) {
					for (Node n : edgeNodes)
						if (n instanceof DAGNode
								&& ((DAGNode) n).getID() != -1
								&& !n.equals(getNodeByID(((DAGNode) n).getID())))
							return new NonExistentErrorEdge((DAGNode) n);
				}

				edge = new DAGEdge(creator, true, edgeNodes);
				boolean result = edges_.add((DAGEdge) edge);
				if (result) {
					if (bFlags.getFlag("ephemeral"))
						addProperty((DAGEdge) edge, EPHEMERAL_MARK, "T");
					else {
						// Trigger modules
						DAGModule<?> rejectedModule = null;
						for (DAGModule<?> module : modules_) {
							if (!module.addEdge((DAGEdge) edge)) {
								rejectedModule = module;
								break;
							}
						}
						if (rejectedModule != null) {
							removeEdge(edge);
							return new ModuleRejectedErrorEdge((DAGEdge) edge,
									rejectedModule);
						}
					}
					changedState_ = true;
				}
			}
			return edge;
		} finally {
			edgeLock_.unlock();
		}
	}

	/**
	 * Convenience method for identifying the boolean args.
	 * 
	 * @param edgeNodes
	 *            The nodes of the edge.
	 * @param creator
	 *            The creator of the edge (can be null).
	 * @param createNew
	 *            If the edge can be created if not found (default false).
	 * @param ephemeral
	 *            If the edge should be created as ephemeral (default false).
	 * @return The created edge or an ErrorEdge if there was an error.
	 */
	public Edge findOrCreateEdge(Node[] edgeNodes, Node creator,
			boolean createNew, boolean ephemeral) {
		return findOrCreateEdge(edgeNodes, creator, new boolean[] { createNew,
				ephemeral });
	}

	/**
	 * Finds or creates a node by parsing the string and searching for a node.
	 * String and Primitive nodes can always be found/created. A node is only
	 * created if a creator is specified. If no node is found, a new node is
	 * created and added.
	 * 
	 * @param nodeStr
	 *            The node string to search with.
	 * @param creator
	 *            If creating new nodes, a creator must be given.
	 * @param flags
	 *            The boolean flags to use during node creation: createNew
	 *            (false), ephemeral (false), dagNodeOnly (false).
	 * @return Either a found node, a created node, or null if impossible to
	 *         parse.
	 */
	public synchronized Node findOrCreateNode(String nodeStr, Node creator,
			boolean... flags) {
		BooleanFlags bFlags = nodeFlags_.loadFlags(flags);
		boolean createNew = bFlags.getFlag("createNew");
		boolean dagNodeOnly = bFlags.getFlag("dagNodeOnly");
		nodeStr = preParseNode(nodeStr, creator, createNew, dagNodeOnly);

		nodeLock_.lock();
		try {
			DAGNode node = null;
			if (nodeStr == null) {
				return null;
			} else if (createNew && nodeStr.isEmpty()) {
				node = new DAGNode(creator);
				if (bFlags.getFlag("ephemeral"))
					addProperty(node, EPHEMERAL_MARK, "T");
				changedState_ = true;
				return node;
			} else if (!dagNodeOnly && nodeStr.startsWith("\"")) {
				return new StringNode(nodeStr);
			} else if (nodeStr.matches("\\d+")) {
				node = getNodeByID(Integer.parseInt(nodeStr));
			} else if (nodeStr.matches("-?\\d+\\.[\\dE+-]+")) {
				return PrimitiveNode.parseNode(nodeStr);
			} else if (!dagNodeOnly && nodeStr.startsWith("'")) {
				return PrimitiveNode.parseNode(nodeStr.substring(1));
			}

			if (node != null)
				return node;

			node = findDAGNode(nodeStr);
			if (node == null && createNew && DAGNode.isValidName(nodeStr)) {
				// Create a new node
				node = new DAGNode(nodeStr, creator);
				boolean result = nodes_.add(node);
				if (result) {
					if (bFlags.getFlag("ephemeral"))
						addProperty(node, EPHEMERAL_MARK, "t");
					// Trigger modules
					for (DAGModule<?> module : modules_)
						module.addNode(node);
					changedState_ = true;
				} else
					return null;
			}
			return node;
		} finally {
			nodeLock_.unlock();
		}
	}

	/**
	 * Convenience node find/creation method with identified boolean args.
	 * 
	 * @param nodeStr
	 *            The name of the node to find/create.
	 * @param creator
	 *            The optional creator.
	 * @param createNew
	 *            If a new node should be created if not found.
	 * @param ephemeral
	 *            If the created node should be ephemeral.
	 * @param dagNodeOnly
	 *            If only DAG nodes can be found/created.
	 * @return The found/created node, or null.
	 */
	public Node findOrCreateNode(String nodeStr, Node creator,
			boolean createNew, boolean ephemeral, boolean dagNodeOnly) {
		return findOrCreateNode(nodeStr, creator, new boolean[] { createNew,
				ephemeral, dagNodeOnly });
	}

	public BufferedWriter getCommandOut() {
		return dagOut_;
	}

	/**
	 * Finds an edge by its ID.
	 * 
	 * @param id
	 *            The ID of the edge.
	 * @return The edge with the provided ID, or null if no edge exists.
	 */
	public DAGEdge getEdgeByID(int id) {
		return edges_.get(id);
	}

	public Collection<DAGEdge> getEdges() {
		return edges_;
	}

	public DAGModule<?> getModule(Class<? extends DAGModule<?>> moduleClass) {
		if (!moduleMap_.containsKey(moduleClass.getCanonicalName())) {
			for (DAGModule<?> mod : modules_) {
				if (moduleClass.isAssignableFrom(mod.getClass())) {
					moduleMap_.put(moduleClass.getCanonicalName(),
							modules_.indexOf(mod));
					return mod;
				}
			}
			return null;
		}
		return modules_.get(moduleMap_.get(moduleClass.getCanonicalName()));
	}

	public ArrayList<DAGModule<?>> getModules() {
		return modules_;
	}

	/**
	 * Finds a node by its ID.
	 * 
	 * @param id
	 *            The ID of the node.
	 * @return The node with the provided ID, or null if no node exists.
	 */
	public DAGNode getNodeByID(int id) {
		return nodes_.get(id);
	}

	public Collection<DAGNode> getNodes() {
		return nodes_;
	}

	public int getNumEdges() {
		return edges_.size();
	}

	public int getNumNodes() {
		return nodes_.size();
	}

	public Edge getRandomEdge() {
		while (DAGEdge.idCounter_ > 0) {
			int maxID = DAGEdge.idCounter_ + 1;
			int id = random_.nextInt(maxID);
			Edge e = getEdgeByID(id);
			if (e != null)
				return e;
		}
		return null;
	}

	public Node getRandomNode() {
		while (DAGNode.idCounter_ > 0) {
			int maxID = DAGNode.idCounter_ + 1;
			int id = random_.nextInt(maxID);
			Node n = getNodeByID(id);
			if (n != null)
				return n;
		}
		return null;
	}

	public synchronized void groundEphemeral() {
		// Compile module properties to remove
		Collection<String> props = compilePertinentProperties();
		props.add(EPHEMERAL_MARK);

		// Run through the nodes, setting them as non-ephemeral
		System.out.print("Grounding ephemeral nodes and edges... ");
		// Run through the edges, reasserting them as non-ephemeral
		SortedSet<DAGEdge> reassertables = orderedReassertables();
		for (DAGEdge e : edges_) {
			if (e.getProperty(EPHEMERAL_MARK) != null) {
				reassertables.add(e);
			}
		}
		for (DAGEdge e : reassertables) {
			// Reassert edge
			try {
				if (!removeEdge(e))
					System.err.println("Error removing ephemeral edge: " + e);
				String creatorStr = e.getCreator();
				Node creator = (creatorStr == null) ? null : findOrCreateNode(
						creatorStr, null);
				Edge e2 = findOrCreateEdge(e.getNodes(), creator, true, false);
				if (e2 instanceof ErrorEdge)
					System.err.println("Error creating once-ephemeral edge: "
							+ e2);
			} catch (Exception ex) {
			}
		}

		for (DAGNode n : nodes_) {
			for (String prop : props)
				n.remove(prop);
		}
		System.out.println("Done!");

		// Rebuild the modules
		reloadModules(true);
	}

	public final void initialise() {
		initialiseInternal();
		boolean saveState = reloadModules(false);
		if (saveState)
			saveState();
	}

	/**
	 * Merges the merging node with the base node. The removes the merging node
	 * and redirects all edges to the base node. If edges cannot be merged, the
	 * process reverts.
	 * 
	 * @param baseNode
	 *            The node that remains.
	 * @param mergingNode
	 *            The node to be merged with the base node.
	 */
	public void mergeNodes(DAGNode baseNode, DAGNode mergingNode) {
		// TODO Complete this method.
		changedState_ = true;
	}

	/**
	 * Parses a DAG Object from a string. This method is used with
	 * readPlainTextFile.
	 * 
	 * @param dagStr
	 *            The string to parse
	 * @param isEdge
	 *            If the object should be an edge.
	 * @param lineNum
	 *            The current line number in the input file
	 * @param input
	 *            The input ine being read.
	 * @return The DAGObject or null if an erroneous parse.
	 */
	public DAGObject parseDAGObject(String dagStr, boolean isEdge) {
		// Parse DAGObject
		dagStr = dagStr.replaceAll("\\t", " ");
		// Replace primitives
		ArrayList<String> split = UtilityMethods.split(dagStr, ' ',
				UtilityMethods.JUST_QUOTE);
		if (split.size() > 1) {
			for (int i = 0; i < split.size(); i++) {
				Matcher m = PRIMITIVE_PATTERN.matcher(split.get(i));
				if (m.matches())
					split.set(i, "'" + split.get(i));
			}
			dagStr = StringUtils.join(split, ' ');
		}

		DAGObject dagObj = null;
		if (isEdge) {
			Node[] nodes = parseNodes(dagStr, null, true, false);
			if (nodes != null) {
				Edge e = findOrCreateEdge(nodes, null, true);
				if (e instanceof ErrorEdge) {
					System.err.println("ErrorEdge: ");
					return null;
				} else
					dagObj = (DAGObject) e;
			}
			if (dagObj == null) {
				System.err.print("NullNodes: ");
				return null;
			}
		} else {
			Node n = findOrCreateNode(dagStr, null, true);
			if (n == null) {
				System.err.print("NullNode: ");
				return null;
			}
			dagObj = (DAGObject) n;
		}
		return dagObj;
	}

	public Node[] parseNodes(String strNodes, Node creator,
			boolean createNodes, boolean dagNodeOnly) {
		if (strNodes.startsWith("("))
			strNodes = UtilityMethods.shrinkString(strNodes, 1);
		ArrayList<String> split = UtilityMethods.split(strNodes, ' ');

		Node[] nodes = new Node[split.size()];
		int i = 0;
		for (String arg : split) {
			nodes[i] = findOrCreateNode(arg, creator, createNodes, false,
					dagNodeOnly);

			if (nodes[i] == null) {
				return null;
			}
			i++;
		}
		return nodes;
	}

	public boolean reloadModules(boolean forceRebuild) {
		boolean saveState = false;
		for (DAGModule<?> module : modules_)
			saveState |= module.initialisationComplete(nodes_, edges_,
					forceRebuild);
		return saveState;
	}

	/**
	 * Removes an edge from the DAG.
	 * 
	 * @param edge
	 *            The edge to be removed.
	 * @return True if the edge was removed.
	 */
	public synchronized boolean removeEdge(Edge edge) {
		if (edge == null)
			return false;

		// Remove the edge
		edgeLock_.lock();
		try {
			boolean result = edges_.remove(edge);

			if (result && ((DAGEdge) edge).getProperty(EPHEMERAL_MARK) == null) {
				// Trigger modules
				for (DAGModule<?> module : modules_)
					module.removeEdge((DAGEdge) edge);
				changedState_ = true;
			}
			return result;
		} finally {
			edgeLock_.unlock();
		}
	}

	/**
	 * Removes an edge from the DAG.
	 * 
	 * @param edgeID
	 *            The ID of the edge to be removed.
	 * @return True if the edge was removed.
	 */
	public synchronized boolean removeEdge(int edgeID) {
		return removeEdge(getEdgeByID(edgeID));
	}

	/**
	 * Removes a node from the DAG, and also removes all information associated
	 * with the node.
	 * 
	 * @param node
	 *            The node to be removed.
	 * @return True if the node was removed.
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean removeNode(DAGNode node) {
		if (node == null)
			return false;

		// Remove node
		nodeLock_.lock();
		try {
			boolean result = nodes_.remove(node);

			if (result) {
				// Remove edges associated with node.
				if (moduleMap_.containsKey(RelatedEdgeModule.class
						.getCanonicalName())) {
					Collection<DAGEdge> relatedEdges = (Collection<DAGEdge>) getModule(
							RelatedEdgeModule.class).execute(node);
					for (DAGEdge edge : relatedEdges)
						removeEdge(edge);
				} else {
					Collection<DAGEdge> removed = new ArrayList<>();
					for (DAGEdge edge : edges_) {
						if (edge.containsNode(node))
							removed.add(edge);
					}

					for (DAGEdge edge : removed)
						removeEdge(edge);
				}

				// Trigger modules
				for (DAGModule<?> module : modules_)
					module.removeNode(node);
				changedState_ = true;
			}
			return result;
		} finally {
			nodeLock_.unlock();
		}
	}

	/**
	 * Removes a node from the DAG, and also removes all information associated
	 * with the node.
	 * 
	 * @param nodeID
	 *            The ID of the node to be removed.
	 * @return True if the node was removed.
	 */
	public synchronized boolean removeNode(int nodeID) {
		return removeNode(getNodeByID(nodeID));
	}

	public synchronized void removeProperty(DAGObject dagObj, String key) {
		dagObj.remove(key);

		for (DAGModule<?> module : modules_)
			module.removeProperty(dagObj, key);
		changedState_ = true;
	}

	public synchronized void saveState() {
		if (!changedState_)
			return;
		// Save 'global' values
		System.out.print("Please wait while saving state... ");
		try {
			((FSTSerialisationMechanism) SerialisationMechanism.FST
					.getSerialiser()).reset();
			FSTSerialisationMechanism.conf.get().registerSerializer(
					DAGObject.class, new FSTDAGObjectSerialiser(), true);
			dagOut_.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File globals = new File(rootDir_, GLOBALS_FILE);
		try {
			globals.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(globals));
			writeDAGDetails(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Save node and edge collections
		try {
			saveDAGFile(nodes_, rootDir_, NODE_FILE, MAX_OBJ_SERIALISATION);
			savePlainTextDAGFile(nodes_, rootDir_, NODE_TXT_FILE,
					MAX_OBJ_SERIALISATION);
			saveDAGFile(edges_, rootDir_, EDGE_FILE, MAX_OBJ_SERIALISATION);
			savePlainTextDAGFile(edges_, rootDir_, EDGE_TXT_FILE,
					MAX_OBJ_SERIALISATION);
		} catch (IOException e) {
			System.err.println("Error serialising DAG.");
		}

		// Save modules
		Set<DAGModule<?>> saved = new HashSet<>();
		for (DAGModule<?> module : modules_) {
			if (!saved.contains(module))
				module.saveModule(rootDir_);
			saved.add(module);
		}

		System.out.println("Done!");
	}

	public void shutdown(boolean sync) {
		if (sync) {
			System.out.println("Saving state and shutting down.");
			saveState();
		}
		System.out.println("Goodbye.");
		System.exit(0);
	}

	public synchronized void writeCommand(String command) {
		try {
			dagOut_.write(command + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		// Node flags
		nodeFlags_ = new BooleanFlags();
		nodeFlags_.addFlag("createNew", false);
		nodeFlags_.addFlag("ephemeral", false);
		nodeFlags_.addFlag("dagNodeOnly", false);

		// Edge flags
		edgeFlags_ = new BooleanFlags();
		edgeFlags_.addFlag("createNew", false);
		edgeFlags_.addFlag("ephemeral", false);
	}
}
