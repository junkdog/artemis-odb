package com.artemis;

import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;

public abstract class BasePrefab<DATA, SERIALIZER extends WorldSerializationManager.ArtemisSerializer> {
	protected final World world;
	protected final DATA data;

	public BasePrefab(World world, DATA data) {
		this.world = world;
		this.data = data;

		world.inject(this);
	}

	public final SaveFileFormat create() {
		return create(serializationManager(world), saveFileFormat());
	}

	protected abstract <T extends SaveFileFormat> T create(SERIALIZER serializer,
	                                                       Class<T> saveFileFormatClass);

	protected Class<SaveFileFormat> saveFileFormat() {
		return SaveFileFormat.class;
	}

	private SERIALIZER serializationManager(World world) {
		WorldSerializationManager wsm = world.getSystem(WorldSerializationManager.class);
		if (wsm == null)
			throw new MundaneWireException(WorldSerializationManager.class);

		return wsm.getSerializer();
	}
}
