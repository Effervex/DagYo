package graph.core;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Pattern;

import util.UtilityMethods;

/**
 * A node is a unique object representing a single 'concept'. Each node has a
 * unique name and id.
 * 
 * @author Sam Sarjant
 */
public class DAGNode extends DAGObject implements Node {
	/** The counter for assigning ids to edges. */
	public static long idCounter_ = 1;

	private static final long serialVersionUID = 2072866863770254720L;

	public static final Pattern QUOTED_NAME = Pattern
			.compile("\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"");

	public static final Pattern UNSPACED_NAME = Pattern.compile("[^\\s()'?].+");

	public static final Pattern VALID_NAME = Pattern.compile("(?:"
			+ QUOTED_NAME.pattern() + ")|" + "(?:" + UNSPACED_NAME.pattern()
			+ ")");

	public static final Pattern NAME_OR_ID = Pattern.compile("(?:'?\\d+)|"
			+ "(?:" + QUOTED_NAME.pattern() + ")|(?:" + UNSPACED_NAME.pattern()
			+ ")");

	private static final String ANON_TO_STRING = "__ANON__";

	/** The unique name of the node. */
	protected String nodeName_;

	public DAGNode() {
		super(null);
		id_ = idCounter_++;
	}

	public DAGNode(Node creator) {
		super(creator);
		id_ = idCounter_++;
	}

	public DAGNode(String name) {
		this(name, null);
	}

	public DAGNode(String name, Node creator) {
		super(creator);
		id_ = idCounter_++;

		if (!name.matches(VALID_NAME.pattern())) {
			System.err.println("Node name:" + name
					+ " is invalid. Must not start with [ ()'?].");
			name = "INVALID_NAME" + name;
		}
		if (name.startsWith("\"") && name.endsWith("\""))
			name = UtilityMethods.shrinkString(name, 1);
		nodeName_ = name;
	}

	@Override
	public String getName() {
		if (isAnonymous())
			return ANON_TO_STRING + id_;
		return nodeName_;
	}

	public boolean isAnonymous() {
		return nodeName_ == null;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static boolean isValidName(String nodeStr) {
		return nodeStr.matches(VALID_NAME.pattern());
	}

	@Override
	protected void readFullObject(ObjectInput in) throws IOException,
			ClassNotFoundException {
		nodeName_ = (String) in.readObject();
	}

	@Override
	protected void writeFullObject(ObjectOutput out) throws IOException {
		out.writeObject(nodeName_);
	}
}
