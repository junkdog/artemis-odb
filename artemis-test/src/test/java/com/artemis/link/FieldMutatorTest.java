package com.artemis.link;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FieldMutatorTest {
	@Test
	public void read_entity_id() throws Exception {
		LinkFactoryTest.LttEntityId c = new LinkFactoryTest.LttEntityId();
		c.id = 1337;

		IntFieldMutator mutator = new IntFieldMutator();
		assertEquals(1337, mutator.read(c, field(c, "id")));
	}

	@Test
	public void write_entity_id() throws Exception {
		LinkFactoryTest.LttEntityId c = new LinkFactoryTest.LttEntityId();

		IntFieldMutator mutator = new IntFieldMutator();
		mutator.write(1337, c, field(c, "id"));

		assertEquals(1337, mutator.read(c, field(c, "id")));
		assertEquals(1337, c.id);
	}

	@Test
	public void read_entity() throws Exception {
		World w = new World();
		w.create();
		w.create();
		Entity e = w.createEntity();

		LinkFactoryTest.LttEntity c = new LinkFactoryTest.LttEntity();
		c.entity = e;

		EntityFieldMutator mutator = new EntityFieldMutator();
		mutator.setWorld(w);
		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
	}

	@Test
	public void write_entity() throws Exception {
		World w = new World();
		w.create();
		w.create();
		Entity e = w.createEntity();

		LinkFactoryTest.LttEntity c = new LinkFactoryTest.LttEntity();

		EntityFieldMutator mutator = new EntityFieldMutator();
		mutator.setWorld(w);
		mutator.write(e.getId(), c, field(c, "entity"));

		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
		assertEquals(e, c.entity);

		mutator.write(-1, c, field(c, "entity"));

		assertEquals(-1, mutator.read(c, field(c, "entity")));
		assertNull(c.entity);
	}


	@Test
	public void read_int_bag() throws Exception {
		World w = new World();
		LinkFactoryTest.LttIntBag c = new LinkFactoryTest.LttIntBag();
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

	@Test
	public void read_entity_bag() throws Exception {
		World w = new World();
		LinkFactoryTest.LttBagEntity c = new LinkFactoryTest.LttBagEntity();
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