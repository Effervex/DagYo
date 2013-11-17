package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.activity.InvalidActivityException;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

public class FSTSerialisationMechanism extends DefaultSerialisationMechanism {
	public static FSTConfiguration conf = FSTConfiguration
			.createDefaultConfiguration();

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

			FSTObjectInput fstIn = conf.getObjectInput(in);
			Object result = fstIn.readObject();
			in.close(); // required !
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	public void serialize(Object object, File location)
			throws InvalidActivityException {
		location.getParentFile().mkdirs();
		try {
			location.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(location));
			FSTObjectOutput fstOut = conf.getObjectOutput(out);
			fstOut.writeObject(object);
			fstOut.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
