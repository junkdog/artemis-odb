package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.PooledComponent;
import com.artemis.EntityWorld;

public class PooledResetTest {
	
	private EntityWorld world;

	@Before
	public void setup() {
		world = new EntityWorld();
	}
	
	@Test
	public void test_common_collection_fields_are_reset() {
		assertEquals(PooledComponent.class, PooledPosition.class.getSuperclass());
		
		Entity e = world.createEntity();
		PooledCollections pc = e.edit().create(PooledCollections.class);
		pc.setAll();

		world.process();
		e.deleteFromWorld();
		world.process();
		
		Entity e2 = world.createEntity();
		PooledCollections pc2 = e2.edit().create(PooledCollections.class);
		
		assertTrue(pc == pc2);
		assertEquals(0, pc2.array.size);
		assertEquals(0, pc2.arrayList.size());
		assertEquals(0, pc2.bag.size());
		assertEquals(0, pc2.hashMap.size());
		assertEquals(0, pc2.hashSet.size());
		assertEquals(0, pc2.intBag.size());
		assertEquals(0, pc2.list.size());
		assertEquals(0, pc2.map.size());
		assertEquals(0, pc2.objectMap.size);
	}

	@Test
	public void test_string_fields_are_reset() {
		assertEquals(PooledComponent.class, PooledString.class.getSuperclass());

		Entity e = world.createEntity();
		PooledString ps = e.edit().create(PooledString.class);
		ps.s = "i'm instanced";

		world.process();
		e.deleteFromWorld();
		world.process();

		Entity e2 = world.createEntity();
		PooledString ps2 = e2.edit().create(PooledString.class);

		assertTrue(ps == ps2);
		assertNull(ps.s);
	}

	@Test
	public void test_primitive_fields_are_reset() {
		assertEquals(PooledComponent.class, PooledCollections.class.getSuperclass());

		Entity e = world.createEntity();
		PooledPosition c = createPosition(e);

		world.process();
		e.deleteFromWorld();
		world.process();

		Entity e2 = world.createEntity();
		PooledPosition c2 = createPosition(e);

		assertTrue(c == c2);
	}

	@Test
	public void test_complex_fields_are_not_nulled() {
		Assert.assertEquals(PooledComponent.class, PooledObjectPosition.class.getSuperclass());
		
		Entity e = world.createEntity();
		PooledObjectPosition c = e.edit().create(PooledObjectPosition.class);
		assertEquals(0, c.vec2.x, 0.0001f);
		assertEquals(0, c.vec2.x, 0.0001f);
		
		c.vec2.x = 2;
		c.vec2.y = 3;
		
		world.process();
		e.deleteFromWorld();
		world.process();
		
		Entity e2 = world.createEntity();
		PooledObjectPosition c2 = e2.edit().create(PooledObjectPosition.class);
		assertEquals(2, c.vec2.x, 0.0001f);
		assertEquals(3, c.vec2.y, 0.0001f);
		
		assertTrue(c == c2);
	}
	
	private static PooledPosition createPosition(Entity e) {
		PooledPosition c = e.edit().create(PooledPosition.class);
		assertEquals(0, c.x, 0.0001f);
		assertEquals(0, c.y, 0.0001f);
		c.x = 2;
		c.y = 2;
		

		return c;
	}
}
