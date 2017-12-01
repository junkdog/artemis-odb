package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CustomJsonWorldSerializationManagerTest extends AbstractCustomWorldSerializationManagerTest {

	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		backend.register(AbstractCustomWorldSerializationManagerTest.SerializedSystem.class, new SerializedSystemSerializer(world));
		return backend;
	}

	public static class SerializedSystemSerializer implements JsonSerializer<SerializedSystem> {
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

	public static class CustomSaveFormat extends SaveFileFormat {
		public SerializedSystem serialized;
		public DummySegment noSerializer;

		public CustomSaveFormat(EntitySubscription es, String dummyString, int dummyNumber) {
			super(es);
			noSerializer = new DummySegment(dummyString, dummyNumber);
		}

		private CustomSaveFormat() {
			super((IntBag)null);
		}
	}
}
