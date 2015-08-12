package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.IdentityHashMap;

@Wire
public class EntitySerializerTest extends GWTTestCase {
	private World world;
	private AspectSubscriptionManager subscriptions;

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_read_write_read_entity() {
		world = new World();
		world.inject(this);

		EntitySubscription subscription = subscriptions.get(Aspect.all());

		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class).text = "hello";
		ee.create(ComponentY.class).text = "whatever";
		ee.create(ReusedComponent.class);

		Entity e = ee.getEntity();

		world.process();

		Json json = new Json(JsonWriter.OutputType.javascript);
		json.setSerializer(IdentityHashMap.class, new ComponentLookupSerializer(world));
		EntitySerializer serializer = new EntitySerializer(world, new ReferenceTracker());
		json.setSerializer(Entity.class, serializer);

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