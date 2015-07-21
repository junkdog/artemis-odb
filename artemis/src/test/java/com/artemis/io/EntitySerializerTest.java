package com.artemis.io;

import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.junit.Test;

import java.util.IdentityHashMap;

import static org.junit.Assert.*;

public class EntitySerializerTest {
	@Test
	public void read_write_read_entity() {
		World world = new World();
		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class).text = "hello";
		ee.create(ComponentY.class).text = "whatever";
		ee.create(ReusedComponent.class);

		Entity e = ee.getEntity();

		world.process();

		Json json = new Json(JsonWriter.OutputType.javascript);
		json.setSerializer(IdentityHashMap.class, new ComponentLookupSerializer(world));
		json.setSerializer(Entity.class, new EntitySerializer(world));

		String s = json.prettyPrint(e);
		System.out.println(s);
	}
}