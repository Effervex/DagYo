package graph.core.cli;

import gnu.trove.iterator.TIntObjectIterator;
import graph.core.DAGNode;
import graph.core.DAGObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.UtilityMethods;
import util.collection.trove.TIndexedCollection;

public class SearchPropertyCommand extends CollectionCommand {
	@Override
	public String helpText() {
		return "{0} N/E propertykey showMatch? (newline) regex : "
				+ "Searches through all nodes/edges for a property "
				+ "with the value matching the regex. Returns a list "
				+ "of all matches, with optional matching value if "
				+ "enabled. This is realtime, so it may be slow.";
	}

	@Override
	public String shortDescription() {
		return "Search all nodes/edges for a property with the given value.";
	}

	@Override
	protected void executeImpl() {
		super.executeImpl();
		DAGPortHandler dagHandler = (DAGPortHandler) handler;

		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		ArrayList<String> split = UtilityMethods.split(data, ' ');
		if (split.size() < 2) {
			print("-1|Wrong number of arguments! Expecting 'N/E propertykey showMatch?'\n");
			return;
		}

		// Nodes or Edges?
		TIndexedCollection<? extends DAGObject> dagObjs = null;
		if (split.get(0).equalsIgnoreCase("N"))
			dagObjs = dagHandler.getDAG().getNodes();
		else if (split.get(0).equalsIgnoreCase("E"))
			dagObjs = dagHandler.getDAG().getEdges();
		else {
			print("-1|First argument must be 'N' or 'E'!'\n");
			return;
		}

		// Property key
		String key = split.get(1);
		if (key.matches(DAGNode.QUOTED_NAME.pattern()))
			key = UtilityMethods.shrinkString(key, 1);
		boolean showMatch = false;
		if (split.size() == 3)
			showMatch = split.get(2).equalsIgnoreCase("T");

		// Read in regex
		try {
			BufferedReader in = dagHandler.getReader();
			String regexStr = in.readLine().trim();
			Pattern regex = Pattern.compile(regexStr);

			// Run through DAG objects, searching for property
			Collection<DAGObject> matches = new ArrayList<>();
			TIntObjectIterator<? extends DAGObject> iter = dagObjs
					.iterator();
			for (int i = dagObjs.size(); i-- > 0;) {
				iter.advance();
				DAGObject dagObj = iter.value();
				String value = dagObj.getProperty(key);
				if (value != null) {
					Matcher m = regex.matcher(value);
					if (m.matches()) {
						matches.add(dagObj);
					}
				}
			}

			matches = dagHandler.postProcess(matches, rangeStart_, rangeEnd_,
					true);
			// Print
			print(matches.size() + "|");
			for (DAGObject match : matches) {
				print(dagHandler.textIDObject(match) + "|");
				if (showMatch)
					print(match.getProperty(key) + "|");
			}
			print("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}