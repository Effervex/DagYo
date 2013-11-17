package util;

public enum SerialisationMechanism {
	DEFAULT(new DefaultSerialisationMechanism()), FST(
			new FSTSerialisationMechanism()), ADDITIVE_THREAD(
			new ThreadedSerialisationMechanism()), OFF(null);

	private DefaultSerialisationMechanism serialiser_;

	private SerialisationMechanism(DefaultSerialisationMechanism serialiser) {
		serialiser_ = serialiser;
	}

	public DefaultSerialisationMechanism getSerialiser() {
		return serialiser_;
	}
}
