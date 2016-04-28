package com.artemis.link;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import org.junit.Test;


import static org.junit.Assert.*;

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

		EntityFieldMutator mutator = new EntityFieldMutator(w);
		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
	}

	@Test
	public void write_entity() throws Exception {
		World w = new World();
		w.create();
		w.create();
		Entity e = w.createEntity();

		LinkFactoryTest.LttEntity c = new LinkFactoryTest.LttEntity();

		EntityFieldMutator mutator = new EntityFieldMutator(w);
		mutator.write(e.getId(), c, field(c, "entity"));

		assertEquals(e.getId(), mutator.read(c, field(c, "entity")));
		assertEquals(e, c.entity);

		mutator.write(-1, c, field(c, "entity"));

		assertEquals(-1, mutator.read(c, field(c, "entity")));
		assertNull(c.entity);
	}

	private static Field field(Object object, String field) throws ReflectionException {
		return ClassReflection.getDeclaredField(object.getClass(), field);
	}
}