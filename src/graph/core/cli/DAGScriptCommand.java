package graph.core.cli;

import commands.ScriptCommand;

public class DAGScriptCommand extends ScriptCommand {
	@Override
	public void execute() {
		DAGPortHandler dagHandler = (DAGPortHandler) handler;
		// TODO Change this so the flags are set to ephemeral instead.
		dagHandler.getDAG().noChecks_ = true;
		super.execute();
		dagHandler.getDAG().noChecks_ = false;
	}
}
