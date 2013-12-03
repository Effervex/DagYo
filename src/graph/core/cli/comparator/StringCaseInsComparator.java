package graph.core.cli.comparator;

public class StringCaseInsComparator extends DefaultComparator {
	@Override
	protected int compareInternal(Object o1, Object o2) {
		return o1.toString().toLowerCase()
				.compareTo(o2.toString().toLowerCase());
	}

}
