package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonWriter;
import com.esotericsoftware.jsonbeans.OutputType;
import org.junit.Test;

import java.util.IdentityHashMap;

import static org.junit.Assert.*;

@Wire
public class EntitySerializerTest {
	private World world;
	private AspectSubscriptionManager subscriptions;

	@Test
	public void read_write_read_entity() {
		world = new World();
		world.inject(this);

		EntitySubscription subscription = subscriptions.get(Aspect.all());

		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class).text = "hello";
		ee.create(ComponentY.class).text = "whatever";
		ee.create(ReusedComponent.class);

		Entity e = ee.getEntity();

		world.process();

		Json json = new Json(OutputType.javascript);
		json.setSerializer(IdentityHashMap.class, new ComponentLookupSerializer(world));
		json.setSerializer(Entity.class, new EntitySerializer(world, new ReferenceTracker()));

		String s = json.prettyPrint(e);

		deleteAll(subscription);

		Entity entity = json.fromJson(Entity.class, s);
		world.process();

		assertNotNull(entity);
		assertEquals("hello", entity.getComponent(ComponentX.class).text);
		assertEquals("whatever", entity.getComponent(ComponentY.class).text);
		assertNotNull(entity.getComponent(ReusedComponent.class));
	}

	private void deleteAll(EntitySubscription sub) {
		IntBag entities = sub.getEntities();
		for (int i = 0; entities.size() > i; i++) {
			world.deleteEntity(entities.get(i));
		}
		world.process();
	}
}