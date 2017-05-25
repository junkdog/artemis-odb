package com.artemis.link;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.component.LttBagEntity;
import com.artemis.component.LttEntity;
import com.artemis.component.LttEntityId;
import com.artemis.component.LttIntBag;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import com.google.gwt.junit.client.GWTTestCase;

public class FieldMutatorTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_read_entity_id() throws Exception {
		LttEntityId c = new LttEntityId();
		c.id = 1337;

		IntFieldMutator mutator = new IntFieldMutator();
		assertEquals(1337, mutator.read(c, field(c, "id")));
	}

	public void test_write_entity_id() throws Exception {
		LttEntityId c = new LttEntityId();

		IntFieldMutator mutator = new IntFieldMutator();
		mutator.write(1337, c, field(c, "id"));

		assertEquals(1337, mutator.read(c, field(c, "id")));
		assertEquals(1337, c.id);
	}

	public void test_read_entity() throws Exception {
		World w = new World();
		w.create();
		w.create();
		Entity e = w.createEntity();

		LttEntity c = new LttEntity();
		c.entity = e;

		EntityFieldMutator mutator = new EntityFieldMutator();
		mutator.setWorld(w);
		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
	}

	public void test_read_int_bag() throws Exception {
		World w = new World();
		LttIntBag c = new LttIntBag();
		c.ids.add(20);
		c.ids.add(30);
		c.ids.add(40);

		IntBag ids = new IntBag();
		ids.addAll(c.ids);

		IntBagFieldMutator mutator = new IntBagFieldMutator();
		mutator.setWorld(w);
		assertEquals(ids, mutator.read(c, field(c, "ids")));

		c.ids.remove(1);
		ids.remove(1);

		assertEquals(ids, mutator.read(c, field(c, "ids")));
	}


	public void test_write_entity() throws Exception {
		World w = new World();
		w.create();
		w.create();
		Entity e = w.createEntity();

		LttEntity c = new LttEntity();

		EntityFieldMutator mutator = new EntityFieldMutator();
		mutator.setWorld(w);
		mutator.write(e.getId(), c, field(c, "entity"));

		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
		assertEquals(e, c.entity);

		mutator.write(-1, c, field(c, "entity"));

		assertEquals(-1, mutator.read(c, field(c, "entity")));
		assertNull(c.entity);
	}

	public void test_read_entity_bag() throws Exception {
		World w = new World();
		LttBagEntity c = new LttBagEntity();
		c.entities.add(w.createEntity());
		c.entities.add(w.createEntity());
		c.entities.add(w.createEntity());

		Bag<Entity> entities = new Bag<Entity>();
		entities.addAll(c.entities);

		EntityBagFieldMutator mutator = new EntityBagFieldMutator();
		mutator.setWorld(w);
		assertEquals(entities, mutator.read(c, field(c, "entities")));

		c.entities.remove(1);
		entities.remove(1);

		assertEquals(entities, mutator.read(c, field(c, "entities")));
	}

	private static Field field(Object object, String field) throws ReflectionException {
		return ClassReflection.getDeclaredField(object.getClass(), field);
	}
}
