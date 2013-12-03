package graph.core.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Command;

public abstract class CollectionCommand extends Command {
	private static final Pattern RANGE_PATTERN = Pattern
			.compile("(.+?) \\[(\\d+), *(\\d+)\\)");
	protected int rangeStart_ = 0;
	protected int rangeEnd_ = Integer.MAX_VALUE;

	@Override
	protected void executeImpl() {
		if (data.isEmpty()) {
			printErrorNoData();
			return;
		}

		Matcher m = RANGE_PATTERN.matcher(data);
		if (m.matches()) {
			rangeStart_ = Integer.parseInt(m.group(2));
			rangeEnd_ = Integer.parseInt(m.group(3));
			data = m.group(1).trim();

			if (rangeEnd_ <= rangeStart_) {
				print("-2|Invalid range argument.\n");
				return;
			}
		}
	}

}
