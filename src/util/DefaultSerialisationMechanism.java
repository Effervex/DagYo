package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.activity.InvalidActivityException;

public class DefaultSerialisationMechanism {
	protected static Map<Long, Boolean> idThreads_ = new ConcurrentHashMap<>();

	/**
	 * Deserializes a file from a given location into the parameter argument.
	 * 
	 * @param location
	 *            The location of the file.
	 * @return A deserialized form of the file.
	 * @throws Exception
	 *             Should something go awry...
	 */
	public Object deserialize(File location) throws InvalidActivityException {
		if (!location.exists())
			return null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					location));

			Object result = in.readObject();
			in.close(); // required !
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean idSerialise() {
		return idThreads_.containsKey(Thread.currentThread().getId());
	}

	/**
	 * Serializes an object to the standard location based on the object's ID.
	 * 
	 * @param object
	 *            The object to serialize.
	 * @return The File created.
	 * @throws Exception
	 *             Should something go awry...
	 */
	public void serialize(Object object, File location, boolean idBased)
			throws InvalidActivityException {
		if (idBased)
			idThreads_.put(Thread.currentThread().getId(), true);
		location.getParentFile().mkdirs();
		try {
			location.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(location));
			out.writeObject(object);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (idBased)
			idThreads_.remove(Thread.currentThread().getId());
	}
}
