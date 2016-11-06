package com.artemis.prefab;

import com.artemis.World;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.esotericsoftware.jsonbeans.JsonValue;

public abstract class Prefab extends BasePrefab<JsonValue, JsonArtemisSerializer> {
	protected Prefab(World world, PrefabReader<JsonValue> reader) {
		super(world, reader);
	}

	@Override
	protected final <T extends SaveFileFormat> T create(JsonArtemisSerializer serializer,
	                                                    JsonValue data,
	                                                    Class<T> saveFileFormatClass) {

		return serializer.load(data, saveFileFormatClass);
	}
}
