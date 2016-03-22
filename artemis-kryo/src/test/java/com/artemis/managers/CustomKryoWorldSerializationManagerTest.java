package com.artemis.managers;

import com.artemis.*;
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CustomKryoWorldSerializationManagerTest {
	private WorldSerializationManager manger;
	private AspectSubscriptionManager subscriptions;
	private SerializedSystem serializedSystem;
	private World world;
	private EntitySubscription allEntities;

	@Before
	public void setup() {

		world = new World(new WorldConfiguration()
				.setSystem(SerializedSystem.class)
				.setSystem(WorldSerializationManager.class));

		world.inject(this);
		KryoArtemisSerializer backend = new KryoArtemisSerializer(world);
		backend.register(SerializedSystem.class, new SerializedSystemSerializer(world));
		manger.setSerializer(backend);

		allEntities = subscriptions.get(Aspect.all());

		world.process();
		assertEquals(0, allEntities.getEntities().size());
	}

	@Test
	public void custom_save_format_save_load() {
		serializedSystem.serializeMe = "dog";

		String b64 = save(allEntities, "a string", 420);
		serializedSystem.serializeMe = "cat";

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());

		ByteArrayInputStream is = new ByteArrayInputStream(
			b64.getBytes(StandardCharsets.UTF_8));
		CustomSaveFormat load = manger.load(is, CustomSaveFormat.class);

		world.process();
		assertEquals("DOG", serializedSystem.serializeMe);
		assertEquals("DOG", load.serialized.serializeMe);
		assertEquals("a string", load.noSerializer.text);
		assertEquals(420, load.noSerializer.number);
	}

	private String save(EntitySubscription subscription, String s, int i) {
		StringWriter writer = new StringWriter();
		SaveFileFormat save = new CustomSaveFormat(subscription, s, i);
		manger.save(writer, save);
		return writer.toString();
	}

	private int deleteAll() {
		IntBag entities = allEntities.getEntities();
		int size = entities.size();
		for (int i = 0; size > i; i++) {
			world.delete(entities.get(i));
		}

		world.process();
		return size;
	}

	public static class SerializedSystem extends BaseSystem {
		public String serializeMe;

		@Override
		protected void processSystem() {}
	}

	public static class DummySegment {
		public String text;
		public int number;

		public DummySegment(String text, int number) {
			this.text = text;
			this.number = number;
		}

		public DummySegment() {}
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
