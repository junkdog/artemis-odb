package com.artemis;

import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Prefab extends BasePrefab<JsonValue, JsonArtemisSerializer> {
	public Prefab(World world, JsonValue value) {
		super(world, value);
	}

	@Override
	protected <T extends SaveFileFormat> T create(JsonArtemisSerializer serializer,
	                                              Class<T> saveFileFormatClass) {

		return serializer.load(data, saveFileFormatClass);
	}
}
