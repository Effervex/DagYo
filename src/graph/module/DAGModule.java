package graph.module;

import graph.core.DAGNode;
import graph.core.DAGObject;
import graph.core.DirectedAcyclicGraph;
import graph.core.Edge;

import java.io.File;
import java.io.Serializable;

import util.DefaultSerialisationMechanism;
import util.SerialisationMechanism;

/**
 * A DAG module defines the basic framework a DAG module must conform to.
 * 
 * @author Sam Sarjant
 */
public abstract class DAGModule<T> implements Serializable {
	private static final String MODULE_DIR = "modules";
	private static final long serialVersionUID = -1752235659675219252L;
	protected transient DirectedAcyclicGraph dag_;
	protected boolean idModule_ = false;

	protected DAGModule() {
		idModule_ = false;
	}

	/**
	 * Called after an edge is added to the DAG.
	 * 
	 * @param edge
	 *            The edge that was added.
	 * @return False if the edge should be removed.
	 */
	public boolean addEdge(Edge edge) {
		return true;
	}

	/**
	 * Called after a node is added to the DAG.
	 * 
	 * @param node
	 *            The node that was added.
	 * @return False if the node should be removed.
	 */
	public boolean addNode(DAGNode node) {
		return true;
	}

	public void addProperty(DAGObject dagObj, String key, String value) {

	}

	public void clear() {

	}

	public abstract T execute(Object... args) throws IllegalArgumentException,
			ModuleException;

	public boolean removeEdge(Edge edge) {
		return false;
	}

	public boolean removeNode(DAGNode node) {
		return false;
	}

	public void removeProperty(DAGObject dagObj, String key) {

	}

	public boolean saveModule(File rootDir) {
		DefaultSerialisationMechanism serialiser = SerialisationMechanism.FST
				.getSerialiser();
		File modFile = moduleFile(rootDir, getClass().getSimpleName());
		try {
			modFile.createNewFile();
			boolean oldID = idModule_;
			// If a module should only save IDs for the nodes/edges.
			idModule_ = true;
			serialiser.serialize(this, modFile, idModule_);
			idModule_ = oldID;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void setDAG(DirectedAcyclicGraph directedAcyclicGraph) {
		dag_ = directedAcyclicGraph;
	}

	private static File moduleFile(File rootDir, String moduleName) {
		File file = new File(rootDir, MODULE_DIR + File.separatorChar
				+ moduleName);
		file.getParentFile().mkdirs();
		return file;
	}

	public static DAGModule<?> loadCreateModule(File rootDir,
			Class<?> moduleClass) throws InstantiationException,
			IllegalAccessException {
		DefaultSerialisationMechanism serialiser = SerialisationMechanism.FST
				.getSerialiser();
		File modFile = moduleFile(rootDir, moduleClass.getSimpleName());
		if (modFile.exists()) {
			try {
				DAGModule<?> module = (DAGModule<?>) serialiser
						.deserialize(modFile);
				return module;
			} catch (Exception e) {
				System.err
						.println("Could not deserialize module. Creating a new one.");
			}
		}

		// TODO In this case, need to rescan dirs.
		return (DAGModule<?>) moduleClass.newInstance();
	}
}
