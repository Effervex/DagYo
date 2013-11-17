package util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activity.InvalidActivityException;

public class ThreadedSerialisationMechanism extends
		DefaultSerialisationMechanism {
	private ExecutorService queue_;

	public ThreadedSerialisationMechanism() {
		queue_ = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors());
	}

	@Override
	public void serialize(Object object, File location, boolean idBased)
			throws InvalidActivityException {
		queue_.execute(new SerialisationTask(object, location, idBased));
	}

	private class SerialisationTask implements Runnable {
		private Object object_;
		private File location_;
		private boolean idBased_;

		public SerialisationTask(Object object, File location, boolean idBased) {
			object_ = object;
			location_ = location;
			idBased_ = idBased;
		}

		@Override
		public void run() {
			try {
				location_.getParentFile().mkdirs();
				location_.createNewFile();
				serialize(object_, location_, idBased_);
			} catch (Exception e) {
			}
		}
	}
}
