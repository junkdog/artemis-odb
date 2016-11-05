package com.artemis.prefab;

import com.artemis.MundaneWireException;
import com.artemis.World;
import com.artemis.annotations.PrefabData;
import com.artemis.io.SaveFileFormat;
import com.artemis.io.SerializationException;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.managers.WorldSerializationManager.ArtemisSerializer;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

public abstract class BasePrefab<DATA, SERIALIZER extends ArtemisSerializer> {
	protected final World world;
	private final PrefabReader<DATA> data;
	private WorldSerializationManager serializationManager;

	protected BasePrefab(World world, PrefabReader<DATA> data) {
		this.world = world;
		this.data = data;

		serializationManager = world.getSystem(WorldSerializationManager.class);
		if (serializationManager == null)
			throw new MundaneWireException(WorldSerializationManager.class);

		world.inject(this);

		// TODO: check if compiled representation exists, else
		// default to current initialization
		data.initialize(getPrefabDataPath());
	}

	private String getPrefabDataPath() {
		try {
			PrefabData pd = ClassReflection.getAnnotation(getClass(), PrefabData.class);
			if (pd != null) {
				return pd.value();
			} else {
				String message = getClass().getName() + " missing @" + PrefabData.class.getSimpleName();
				throw new MissingPrefabDataException(message);
			}
		} catch (ReflectionException e) {
			throw new MissingPrefabDataException(e);
		}
	}

	protected final SaveFileFormat create() {
		SERIALIZER serializer = serializationManager.getSerializer();
		return create(serializer, data.getData(), saveFileFormat());
	}

	protected abstract <T extends SaveFileFormat> T create(SERIALIZER serializer,
	                                                       DATA data,
	                                                       Class<T> saveFileFormatClass);

	protected Class<SaveFileFormat> saveFileFormat() {
		return SaveFileFormat.class;
	}


	public static class MissingPrefabDataException extends SerializationException {
		public MissingPrefabDataException() {
		}

		public MissingPrefabDataException(String message) {
			super(message);
		}

		public MissingPrefabDataException(String message, Throwable cause) {
			super(message, cause);
		}

		public MissingPrefabDataException(Throwable cause) {
			super(cause);
		}
	}
}
