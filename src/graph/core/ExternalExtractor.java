package graph.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import util.UtilityMethods;

/**
 * This class is designed to extract all nodes and edges from a DAG from an
 * external program rather than from the DAG itself. This is to combat
 * OutOfMemory Exceptions when the DAG cannot serialise itself.
 * 
 * @author Sam Sarjant
 */
public class ExternalExtractor {
	private Socket socket_;

	public ExternalExtractor(int port) throws UnknownHostException, IOException {
		socket_ = new Socket("localhost", port);
	}

	public static void main(String[] args) {
		boolean nodes = false;
		boolean edges = false;
		int port = -1;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p"))
				port = Integer.parseInt(args[++i]);
			else if (args[i].equals("-n"))
				nodes = true;
			else if (args[i].equals("-e"))
				edges = true;
		}

		// Default to nodes and edges true
		if (!nodes && !edges) {
			nodes = true;
			edges = true;
		}

		try {
			ExternalExtractor ee = new ExternalExtractor(port);
			ee.extract(nodes, edges);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void extract(boolean nodes, boolean edges) throws Exception {
		PrintWriter out = new PrintWriter(socket_.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket_.getInputStream(), "UTF-8"));

		// Init vars
		out.println("set /env/time false");
		in.readLine();
		out.println("set /env/endmessage --END--");
		in.readLine();
		out.println("set /env/prompt ");
		in.readLine();
		out.println("set /env/pretty true");
		in.readLine();

		// Read the nodes
		if (nodes) {
			File nodeFile = new File("nodeExtract.txt");
			nodeFile.createNewFile();
			extractDAG(false, out, in, nodeFile);
		}
		if (edges) {
			File nodeFile = new File("edgeExtract.txt");
			nodeFile.createNewFile();
			extractDAG(true, out, in, nodeFile);
		}

		in.close();
		out.close();
	}

	/**
	 * Extracts data from the DAG via a series of next commands
	 * 
	 * @param isEdges
	 *            If the collection being extracted is edges (otherwise nodes).
	 * @param out
	 *            The socket writer.
	 * @param in
	 *            The socket reader.
	 * @param file
	 *            The file to write to.
	 * @throws IOException
	 */
	private void extractDAG(boolean isEdges, PrintWriter out,
			BufferedReader in, File file) throws IOException {
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
		String nextCommand = (isEdges) ? "nextedge" : "nextnode";
		String propType = (isEdges) ? "E" : "N";
		ArrayList<String> propIndexMap = new ArrayList<>();

		int index = 0;
		while (index != -1) {
			out.println(nextCommand + " " + index);
			String result = in.readLine();
			in.readLine();
			ArrayList<String> split = UtilityMethods.split(result, '|',
					UtilityMethods.JUST_QUOTE);

			// Index
			index = Integer.parseInt(split.get(0));
			if (index == -1)
				break;

			// DAG Name
			String nodeName = split.get(1);
			nodeName = nodeName.replaceAll("\\t", " ");
			fileOut.write(nodeName);

			// Props
			int lastIndex = 0;
			String[] propMap = new String[20];
			out.println("listprops " + propType + " " + index);
			while (!(result = in.readLine()).equals("--END--")) {
				split = UtilityMethods.split(result, '=');
				String key = UtilityMethods.shrinkString(split.get(0), 1);
				Integer propIndex = propIndexMap.indexOf(key);
				if (propIndex == -1) {
					propIndex = propIndexMap.size();
					propIndexMap.add(key);
				}
				propMap[propIndex] = UtilityMethods.shrinkString(split.get(1),
						1);
				lastIndex = Math.max(lastIndex, propIndex);
			}

			// Writing the props (in order)
			for (int i = 0; i <= lastIndex; i++) {
				fileOut.write("\t");
				if (propMap[i] != null)
					fileOut.write(propMap[i].replaceAll("\\t", " "));
			}
			fileOut.write("\n");
		}

		fileOut.close();

		// Write props at top of file
		File tempFile = new File("temp.txt");
		BufferedWriter tempOut = new BufferedWriter(new FileWriter(tempFile));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		tempOut.write("DAGObject");
		for (String prop : propIndexMap)
			tempOut.write("\t" + prop);
		tempOut.write("\n");

		String input = null;
		while ((input = reader.readLine()) != null)
			tempOut.write(input + "\n");
		reader.close();
		tempOut.close();
		file.delete();
		tempFile.renameTo(file);
	}
}
