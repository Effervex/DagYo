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
package graph.module;

import graph.core.DAGEdge;
import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;
import graph.core.Node;
import graph.core.StringNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import util.AliasedObject;
import util.collection.trove.TIndexedCollection;

/**
 * The node alias module encodes the aliases for a node (be they from node name
 * or alias edges) into an efficient, searchable structure.
 * 
 * @author Sam Sarjant
 */
public class NodeAliasLuceneModule extends DAGModule<Collection<DAGNode>>
		implements AliasModule {
	private static final String INDEX_FOLDER = "NodeAliasLuceneIndex";
	private static final long serialVersionUID = 7451861373081932549L;
	public static final String ALIAS_PROP = "alias";
	public static final String LOWERCASE_FIELD = "l_alias";
	public static final String ORIGINAL_FIELD = "o_alias";
	public static final String NODE_FIELD = "node";
	public static final String ID_FIELD = "uid";
	private transient IndexWriter writer_;
	private transient QueryParser parser_;
	private transient SearcherManager manager_;

	private String processAlias(String name) {
		name = name.replaceAll("\\s{2,}", " ");
		return name;
	}

	public boolean addAlias(DAGNode node, String alias) {
		if (alias.isEmpty())
			return false;

		// Add the document
		Document doc = new Document();
		doc.add(new StringField("o_alias", alias, Store.YES));
		// Don't need to save the lowercase field
		String processed = alias.toLowerCase();
		processed = processed.replaceAll(" ", "_");
		doc.add(new StringField("l_alias", processed, Store.NO));
		doc.add(new StringField("node", node.getIdentifier(), Store.YES));
		String uniqID = node.getIdentifier() + "-" + alias;
		doc.add(new StringField("uid", uniqID, Store.NO));
		Term term = new Term("uid", uniqID);

		// Don't add if it already exists
		try {
			writer_.updateDocument(term, doc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean addEdge(DAGEdge edge) {
		if (edge.getProperty(ALIAS_PROP) != null) {
			Node[] edgeNodes = edge.getNodes();
			for (int i = 2; i < edgeNodes.length; i++) {
				if (edgeNodes[i] instanceof StringNode) {
					if (edgeNodes[1] instanceof DAGNode)
						addAlias((DAGNode) edgeNodes[1], edgeNodes[i].getName());
					else
						System.err
								.println("Alias edge defined by non-DAG node: "
										+ edge.toString());
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean addNode(DAGNode node) {
		if (!node.isAnonymous())
			addAlias(node, node.getName());
		return true;
	}

	@Override
	public void clear() {
		try {
			writer_.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public Collection<DAGNode> execute(Object... args)
			throws IllegalArgumentException {
		if (args == null || args.length == 0)
			throw new IllegalArgumentException(
					"Requires at least 1 string argument.");
		String alias = (String) args[0];

		boolean caseSensitive = true;
		if (args.length >= 2)
			caseSensitive = (boolean) args[1];

		boolean exactString = true;
		if (args.length >= 3)
			exactString = (boolean) args[2];

		boolean onlyNodeNames = false;
		if (args.length >= 4)
			onlyNodeNames = (boolean) args[3];

		if (onlyNodeNames)
			return findNodeByName(alias, caseSensitive);
		else
			return findNodeByAlias(alias, caseSensitive, exactString, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<AliasedObject<DAGNode>> findAliasedNodes(String alias,
			boolean caseSensitive, boolean exactString) {
		return (Collection<AliasedObject<DAGNode>>) findNodeInternal(alias,
				caseSensitive, exactString, true);
	}

	/**
	 * Find a node(s) by its alias or name.
	 * 
	 * @param alias
	 *            The alias to search with.
	 * @param caseSensitive
	 *            If the alias is case sensitive
	 * @param exactString
	 *            If the alias is exact or represents an prefix substring.
	 * @param excludeEphemeral
	 *            If ephemeral nodes are excluded from the results.
	 * @return All nodes indexed by the given alias.
	 */
	@SuppressWarnings("unchecked")
	public Collection<DAGNode> findNodeByAlias(String alias,
			boolean caseSensitive, boolean exactString, boolean excludeEphemeral) {
		Collection<DAGNode> nodes = (Collection<DAGNode>) findNodeInternal(
				alias, caseSensitive, exactString, false);

		if (excludeEphemeral) {
			Collection<DAGNode> nonEphemeral = new ArrayList<DAGNode>(
					nodes.size());
			for (DAGNode n : nodes)
				if (n.getProperty(DirectedAcyclicGraph.EPHEMERAL_MARK) == null)
					nonEphemeral.add(n);
			nodes = nonEphemeral;
		}

		// Converting ID to node
		return nodes;
	}

	/**
	 * An internal method for merging both the aliased node and regular node
	 * operations. Finds all nodes with a given alias, according to case
	 * sensitive and exact string arguments.
	 *
	 * @param alias
	 *            The alias to search for.
	 * @param caseSensitive
	 *            If the search is case sensitive.
	 * @param exactString
	 *            If the search should be for an exact string.
	 * @param aliased
	 *            If recording the alias information for each match.
	 * @return A collection of nodes, which may be aliased by a character array.
	 */
	protected Collection<?> findNodeInternal(String alias,
			boolean caseSensitive, boolean exactString, boolean aliased) {
		Collection<Object> nodes = new ArrayList<>();
		if (alias.isEmpty())
			return nodes;

		Query query = null;
		try {
			// Query Lucene, and adjust results
			String queryStr = QueryParser.escape(alias.toLowerCase());
			queryStr = queryStr.replaceAll(" ", "_");
			if (!exactString)
				queryStr += "*";
			query = parser_.parse(queryStr);

			// manager_.maybeRefresh();
			IndexSearcher searcher = manager_.acquire();
			try {
				// TODO May need to limit the number of results.
				TopDocs result = searcher.search(query, Integer.MAX_VALUE);

				for (ScoreDoc scoreDoc : result.scoreDocs) {
					Document d = searcher.doc(scoreDoc.doc);
					DAGNode n = parseNode(d.get(NODE_FIELD));
					if (n != null) {
						boolean keep = false;
						String originalString = d.get(ORIGINAL_FIELD);
						if (caseSensitive) {
							if (originalString.startsWith(alias))
								keep = true;
						} else
							keep = true;

						// Recording the node in the collection
						if (keep) {
							if (aliased) {
								nodes.add(new AliasedObject<DAGNode>(
										originalString, n));
							} else
								nodes.add(n);
						}
					}
				}
			} finally {
				manager_.release(searcher);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodes;
	}

	/**
	 * Parses a node from the Lucene identification string.
	 *
	 * @param string
	 *            The node string.
	 * @return A DAGNode represented by the identification string.
	 */
	protected DAGNode parseNode(String string) {
		return dag_.getNodeByID(Integer.parseInt(string));
	}

	/**
	 * Finds a node(s) by its exact name (not alias).
	 * 
	 * @param nodeName
	 *            The name of the node.
	 * @param caseSensitive
	 *            If the search is case sensitive.
	 * @return All nodes with the same exact name.
	 */
	public Collection<DAGNode> findNodeByName(String nodeName,
			boolean caseSensitive) {
		Collection<DAGNode> aliasNodes = findNodeByAlias(
				processAlias(nodeName), caseSensitive, true, false);
		Collection<DAGNode> namedNodes = new HashSet<>(aliasNodes.size());
		for (DAGNode aliasNode : aliasNodes) {
			if (caseSensitive && aliasNode.getName().equals(nodeName)
					|| !caseSensitive
					&& aliasNode.getName().equalsIgnoreCase(nodeName))
				namedNodes.add(aliasNode);
		}
		return namedNodes;
	}

	@Override
	public Collection<DAGNode> findNodes(String alias, boolean caseSensitive,
			boolean exactString) {
		return execute(alias, caseSensitive, exactString);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean initialisationComplete(TIndexedCollection<DAGNode> nodes,
			TIndexedCollection<DAGEdge> edges, boolean forceRebuild) {
		if (writer_.numDocs() != 0 && !forceRebuild) {
			try {
				writer_.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		// Iterate through all nodes and edges, adding aliases
		System.out.print("Rebuilding alias Lucene index... ");
		clear();
		defaultRebuild(nodes, true, edges, true);
		System.out.println("Done!");
		return true;
	}

	public boolean removeAlias(DAGNode node, String alias) {
		alias = processAlias(alias);
		if (alias.isEmpty())
			return false;
		String uniqID = node.getIdentifier() + "-" + alias;
		Term term = new Term("uid", uniqID);
		try {
			writer_.deleteDocuments(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean removeEdge(DAGEdge edge) {
		if (edge.getProperty(ALIAS_PROP) != null) {
			Node[] edgeNodes = edge.getNodes();
			for (int i = 2; i < edgeNodes.length; i++) {
				removeAlias((DAGNode) edgeNodes[1], edgeNodes[i].getName());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean removeNode(DAGNode node) {
		if (!node.isAnonymous())
			return removeAlias(node, node.getName());
		return true;
	}

	@Override
	public void setDAG(DirectedAcyclicGraph directedAcyclicGraph) {
		super.setDAG(directedAcyclicGraph);

		// Connect to the Lucene DB
		try {
			Analyzer analyser = new KeywordAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyser);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			Path path = DAGModule.moduleFile(directedAcyclicGraph.rootDir_,
					INDEX_FOLDER).toPath();
			Directory directory = FSDirectory.open(path);
			// Directory directory = new RAMDirectory();
			writer_ = new IndexWriter(directory, config);

			// Searching
			parser_ = new QueryParser(LOWERCASE_FIELD, analyser);
			manager_ = new SearcherManager(writer_, true, new SearcherFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean supportsEdge(DAGEdge edge) {
		return true;
	}

	@Override
	public boolean supportsNode(DAGNode node) {
		return true;
	}

	@Override
	public String toString() {
		return "Node Alias Module: "
				+ writer_.numDocs()
				+ " entries ("
				+ String.format("%.2f", writer_.ramBytesUsed() / (1024 * 1024f))
				+ "MB)";
	}
}
