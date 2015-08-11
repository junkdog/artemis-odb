package com.artemis.managers;

import com.artemis.*;
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@Wire
public class CustomJsonWorldSerializationManagerTest {
	private WorldSerializationManager manger;
	private AspectSubscriptionManager subscriptions;
	private SerializedSystem serializedSystem;
	private World world;
	private EntitySubscription allEntities;

	@Before
	public void setup() {

		world = new World(new WorldConfiguration()
				.setSystem(SerializedSystem.class)
				.setManager(WorldSerializationManager.class));

		world.inject(this);
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		backend.register(SerializedSystem.class, new SerializedSystemSerializer(world));
		manger.setSerializer(backend);

		allEntities = subscriptions.get(Aspect.all());

		world.process();
		assertEquals(0, allEntities.getEntities().size());
	}

	@Test
	public void custom_save_format_save_load() {
		serializedSystem.serializeMe = "dog";

		String json = save(allEntities, "a string", 420);
		serializedSystem.serializeMe = "cat";

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
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
			world.deleteEntity(entities.get(i));
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

	@Wire
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

	@Wire
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
