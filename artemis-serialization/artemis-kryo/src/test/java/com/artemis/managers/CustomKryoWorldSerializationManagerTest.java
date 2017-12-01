package com.artemis.managers;

import com.artemis.*;
import com.artemis.component.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.KryoArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CustomKryoWorldSerializationManagerTest extends AbstractCustomWorldSerializationManagerTest {

	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		KryoArtemisSerializer backend = new KryoArtemisSerializer(world);
		backend.register(SerializedSystem.class, new SerializedSystemSerializer(world));
		backend.register(CustomSaveFormat.class);
		backend.register(CustomKryoWorldSerializationManagerTest.DummySegment.class);
		return backend;
	}

	public static class SerializedSystemSerializer extends Serializer<SerializedSystem> {
		private SerializedSystem system;

		public SerializedSystemSerializer(World world) {
			world.inject(this);
		}

		@Override
		public void write (Kryo kryo, Output output, SerializedSystem serializedSystem) {
			output.writeString(serializedSystem.serializeMe);
		}

		@Override
		public SerializedSystem read (Kryo kryo, Input input, Class<SerializedSystem> aClass) {
			// wtf how is this not null?
			system.serializeMe = input.readString().toUpperCase();
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
