package graph.core.cli;

import java.io.BufferedReader;
import java.io.IOException;

import core.Command;

public class ShutdownCommand extends Command {

	@Override
	public String shortDescription() {
		return "Shuts down the entire DAG. Requires correct passphrase.";
	}

	@Override
	protected void executeImpl() {
		BufferedReader in = getPortHandler().getReader();
		String passphrase;
		try {
			passphrase = in.readLine();
			if (passphrase.equals("waikatoKM")) {
				print("Syncing and shutting DAG down.\n");
				new Thread(new Runnable() {
					@Override
					public void run() {
						DAGPortHandler dagHandler = (DAGPortHandler) handler;
						dagHandler.dag_.shutdown();
					}
				}).start();
				handler.terminate();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
