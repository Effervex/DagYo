package graph.core.cli;

import core.Command;

public class SyncCommand extends Command {

	@Override
	public String shortDescription() {
		return "Synchronises the DAG information to file.";
	}

	@Override
	protected void executeImpl() {
		print("Syncing DAG information.\n");
		new Thread(new Runnable() {
			@Override
			public void run() {
				DAGPortHandler dagHandler = (DAGPortHandler) handler;
				dagHandler.dag_.saveState();
			}
		}).start();
	}

}
