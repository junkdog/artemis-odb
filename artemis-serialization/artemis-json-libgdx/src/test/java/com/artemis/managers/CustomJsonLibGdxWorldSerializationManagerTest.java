package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CustomJsonLibGdxWorldSerializationManagerTest extends AbstractCustomWorldSerializationManagerTest {

	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		backend.register(SerializedSystem.class, new SerializedSystemSerializer(world));
		return backend;
	}

	public static class SerializedSystemSerializer implements Json.Serializer<SerializedSystem> {
		private SerializedSystem system;

		public SerializedSystemSerializer(World world) {
			world.inject(this);
		}

		@Override
		public void write(Json json, SerializedSystem ds, Class knownType) {
			json.writeObjectStart();
			json.writeValue("value", ds.serializeMe, knownType);
			json.writeObjectEnd();
		}

		@Override
		public SerializedSystem read(Json json, JsonValue jsonData, Class type) {
			system.serializeMe = json.readValue(String.class, jsonData.child).toUpperCase();
			return system;
		}
	}

}
