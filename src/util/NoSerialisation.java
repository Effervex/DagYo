package util;

import java.io.File;

import javax.activity.InvalidActivityException;

public class NoSerialisation extends DefaultSerialisationMechanism {

	@Override
	public Object deserialize(File location) throws InvalidActivityException {
		throw new InvalidActivityException("No serialisation active.");
	}

	@Override
	public void serialize(Object object, File location, boolean idBased)
			throws InvalidActivityException {
		throw new InvalidActivityException("No serialisation active.");
	}

}
