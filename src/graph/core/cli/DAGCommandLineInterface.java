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
package graph.core.cli;

import graph.core.DAGNode;
import graph.core.DirectedAcyclicGraph;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import core.CommandParser;
import core.CommandQueue;
import core.Main;
import core.PortHandler;

public class DAGCommandLineInterface extends Main {
	public static final Pattern CONTEXT_ARGUMENT_PATTERN = Pattern
			.compile("\\((" + DAGNode.NAME_OR_ID + ")\\)");
	private static final int DEFAULT_PORT_NUMBER = 2425;
	private static CommandLine arguments_;
	protected DirectedAcyclicGraph dag_;

	public DAGCommandLineInterface(int aPort, DirectedAcyclicGraph dag) {
		super(aPort);
		dag_ = dag;

		// Adding core commands
		CommandParser.addCommand("node", NodeCommand.class);
		CommandParser.addCommand("edge", EdgeCommand.class);
		CommandParser.addCommand("addNode", AddNodeCommand.class);
		CommandParser.addCommand("addEdge", AddEdgeCommand.class);
		CommandParser.addCommand("removeNode", RemoveNodeCommand.class);
		CommandParser.addCommand("removeEdge", RemoveEdgeCommand.class);
		CommandParser.addCommand("randomNode", RandomNodeCommand.class);
		CommandParser.addCommand("randomEdge", RandomEdgeCommand.class);
		CommandParser.addCommand("map", SortableMapCommand.class);
		CommandParser.addCommand("numNodes", NumNodeCommand.class);
		CommandParser.addCommand("numEdges", NumEdgeCommand.class);
		CommandParser.addCommand("addProp", AddPropertyCommand.class);
		CommandParser.addCommand("getProp", GetPropertyCommand.class);
		CommandParser.addCommand("removeProp", RemovePropertyCommand.class);
		CommandParser.addCommand("wipeProp", WipePropertyCommand.class);
		CommandParser.addCommand("listProps", ListPropertiesCommand.class);
		CommandParser.addCommand("shutdown", ShutdownCommand.class);
		CommandParser.addCommand("stats", DAGStatsCommand.class);
		CommandParser.addCommand("sync", SyncCommand.class);
		CommandParser.addCommand("groundEphemeral",
				GroundEphemeralCommand.class);
		CommandParser.addCommand("nextNode", NextNodeCommand.class);
		CommandParser.addCommand("nextEdge", NextEdgeCommand.class);
		CommandParser.addCommand("lastNode", LastNodeCommand.class);
		CommandParser.addCommand("lastEdge", LastEdgeCommand.class);
		CommandParser.addCommand("varHelp", DAGVarHelpCommand.class);
		CommandParser.addCommand("script", DAGScriptCommand.class);
		CommandParser.addCommand("export", ExportCommand.class);
	}

	@Override
	protected PortHandler createPortHandler(Socket serverSocket,
			CommandQueue commandQueue) throws IOException {
		return new DAGPortHandler(serverSocket, commandQueue, dag_);
	}

	public static void main(String[] args) {
		try {
			DirectedAcyclicGraph dag = new DirectedAcyclicGraph(
					getRootDir(args));
			new DAGCommandLineInterface(getPort(args), dag).start();

			dag.initialise();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getPort(String[] args) {
		if (arguments_ == null)
			parseArgs(args);

		int port = DEFAULT_PORT_NUMBER;
		if (arguments_.hasOption("p"))
			port = Integer.parseInt(arguments_.getOptionValue("p"));
		return port;
	}

	public static File getRootDir(String[] args) {
		if (arguments_ == null)
			parseArgs(args);

		File root = DirectedAcyclicGraph.DEFAULT_ROOT;
		if (arguments_.hasOption("r"))
			root = new File(arguments_.getOptionValue("r"));
		return root;
	}

	protected static void parseArgs(String[] args) {
		Options options = new Options();
		options.addOption("r", true, "The root directory of the DAG.");
		options.addOption("p", true, "The port number to use.");
		options.addOption("n", true, "The initial hashmap size for the nodes.");
		options.addOption("e", true, "The initial hashmap size for the edges.");

		CommandLineParser parser = new BasicParser();
		try {
			arguments_ = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
