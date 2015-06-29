package graph.core.cli;

import core.Command;
import core.PortHandler;

public abstract class DAGCommand extends Command {
	protected DAGPortHandler dagHandler;

	public void setPortHandler(PortHandler aHandler) {
		super.setPortHandler(aHandler);
		dagHandler = (DAGPortHandler) aHandler;
	}

	@Override
	public void print(String message) {
		if (dagHandler.get(DAGPortHandler.HUMAN).equalsIgnoreCase("true")) {
			message = message.replaceAll(",(?! )", "\t");
			if (message.contains("\t"))
				message = message.replaceAll("\\|", "\n");
		}
		super.print(message);
	}
	
}
