package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@Wire
public class WorldSerializationManagerTest {
	private WorldSerializationManager manger;
	private AspectSubscriptionManager asm;

	@Test
	public void json_serializer_save_load_std_format() {
		World world = new World(new WorldConfiguration()
				.setManager(WorldSerializationManager.class));

		world.inject(this);
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		manger.setSerializer(backend);

		EntitySubscription sub = asm.get(Aspect.all());

		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class).text = "hello";
		ee.create(ComponentY.class).text = "whatever";
		ee.create(ReusedComponent.class);

		EntityEdit ee2 = world.createEntity().edit();
		ee2.create(ComponentX.class).text = "hello 2";
		ee2.create(ComponentY.class).text = "whatever 2";
		ee2.create(ReusedComponent.class);

		EntityEdit ee3 = world.createEntity().edit();
		ee3.create(ComponentX.class).text = "hello 3";
		ee3.create(ComponentY.class).text = "whatever 3";
		ee3.create(ReusedComponent.class);

		world.process();

		assertEquals(3, sub.getEntities().size());

		StringWriter writer = new StringWriter();

		SaveFileFormat save = new SaveFileFormat(asm.get(Aspect.all()));

		manger.save(writer, save);

		ByteArrayInputStream is = new ByteArrayInputStream(
				writer.toString().getBytes(StandardCharsets.UTF_8));

		deleteAll(world, asm);
		assertEquals(0, sub.getEntities().size());

		SaveFileFormat load = manger.load(is, SaveFileFormat.class);

		world.process();
		assertEquals(3, sub.getEntities().size());

		StringWriter writer2 = new StringWriter();
		manger.save(writer2, save);

		deleteAll(world, asm);
		assertEquals(0, sub.getEntities().size());
	}

	private static int deleteAll(World world, AspectSubscriptionManager asm) {
		IntBag entities = asm.get(Aspect.all()).getEntities();
		int size = entities.size();
		for (int i = 0; size > i; i++) {
			world.deleteEntity(entities.get(i));
		}
		world.process();

		return size;
	}
}