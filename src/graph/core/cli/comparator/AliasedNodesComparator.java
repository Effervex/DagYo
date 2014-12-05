package graph.core.cli.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import util.AliasedObject;
import util.UtilityMethods;
import graph.core.DAGNode;
import graph.core.cli.DAGPortHandler;

/**
 * This nodes comparator is special in that it only deals with comparisons
 * between AliasedObjects - all other comparisons are simple caseless toString
 * comparisons.
 *
 * @author Sam Sarjant
 */
public class AliasedNodesComparator implements Comparator<Object> {
	private String alias_;

	public AliasedNodesComparator(DAGPortHandler handler, String alias) {
		if (alias != null && alias.startsWith("\"") && alias.endsWith("\""))
			alias = UtilityMethods.shrinkString(alias, 1);
		alias_ = alias;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		try {
			if (alias_ != null) {
				AliasedObject<Character, DAGNode> ao1 = (AliasedObject<Character, DAGNode>) o1;
				AliasedObject<Character, DAGNode> ao2 = (AliasedObject<Character, DAGNode>) o2;

				// Check same node
				String alias1 = ao1.getAliasString();
				String alias2 = ao2.getAliasString();
				if (ao1.object_.equals(ao2.object_))
					return alias1.compareTo(alias2);

				// First check for exact node
				if (ao1.object_.getName().equals(alias_))
					return -1;
				if (ao2.object_.getName().equals(alias_))
					return 1;

				// Second, check for case-insensitive name
				if (ao1.object_.getName().equalsIgnoreCase(alias_))
					return -1;
				if (ao2.object_.getName().equalsIgnoreCase(alias_))
					return 1;

				// Check same alias
				if (alias1.equals(alias2))
					return ao1.compareTo(ao2);

				// Third, check for exact alias
				if (alias1.equalsIgnoreCase(alias_))
					return -1;
				if (alias2.equalsIgnoreCase(alias_))
					return 1;

				// Fourth, check for case-insensitive name
				if (alias1.equalsIgnoreCase(alias_))
					return -1;
				if (alias2.equalsIgnoreCase(alias_))
					return 1;
				
				// Levenshtein distance
				int ld1 = StringUtils.getLevenshteinDistance(alias1, alias_);
				int ld2 = StringUtils.getLevenshteinDistance(alias2, alias_);
				if (ld1 < ld2)
					return -1;
				if (ld1 > ld2)
					return 1;				

				// Fifth, check prefix
				if (alias1.startsWith(alias_)) {
					if (alias2.startsWith(alias_)) {
						return alias1.compareTo(alias2);
					} else
						return -1;
				} else if (alias2.startsWith(alias_))
					return 1;

				// Sixth, revert to caseless string comparison
			}
		} catch (ClassCastException cce) {
		}
		int result = o1.toString().toLowerCase()
				.compareTo(o2.toString().toLowerCase());
		if (result == 0)
			result = o1.toString().compareTo(o2.toString());
		return result;
	}
}
