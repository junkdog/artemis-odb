package com.artemis.prefab;

import com.artemis.MundaneWireException;
import com.artemis.World;
import com.artemis.annotations.PrefabData;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.managers.WorldSerializationManager.ArtemisSerializer;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

/**
 * Shared functionality for prefabs. {@link #create()} is expected to be wrapped
 * by concrete prefab implementations, e.g. inside <code>PlayerPrefab::create(color, x, y)</code>.
 *
 * @param <DATA> Data source
 * @param <SERIALIZER> Serializer, one of libgdx's or json-beans.
 */
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

		// TODO: #439 - generate .class  from .json
		data.initialize(getPrefabDataPath());
	}

	private String getPrefabDataPath() {
		try {
			PrefabData pd = ClassReflection.getAnnotation(getClass(), PrefabData.class);
			if (pd != null) {
				return pd.value();
			} else {

				String annotation = PrefabData.class.getSimpleName();
				String message = getClass().getName() + " must be annotated with @" + annotation;
				throw new MissingPrefabDataException(message);
			}
		} catch (ReflectionException e) {
			throw new MissingPrefabDataException(e);
		}
	}

	public final SaveFileFormat create() {
		SERIALIZER serializer = serializationManager.getSerializer();
		return create(serializer, data.getData(), saveFileFormat());
	}

	protected abstract <T extends SaveFileFormat> T create(SERIALIZER serializer,
	                                                       DATA data,
	                                                       Class<T> saveFileFormatClass);

	protected Class<SaveFileFormat> saveFileFormat() {
		return SaveFileFormat.class;
	}
}
