package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.artemis.EntityHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.PooledComponent;
import com.artemis.World;

public class PooledResetTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
	}
	
	@Test
	public void test_primitive_fields_are_reset() {
		assertEquals(PooledComponent.class, PooledPosition.class.getSuperclass());
		
		int e = world.createEntity();
		PooledPosition c = createPosition(world,e);
		
		world.process();
		world.deleteEntity(e);
		world.process();
		
		int e2 = world.createEntity();
		PooledPosition c2 = createPosition(world, e);
		
		assertTrue(c == c2);
	}

	@Test
	public void test_complex_fields_are_not_nulled() {
		Assert.assertEquals(PooledComponent.class, PooledObjectPosition.class.getSuperclass());
		
		int e = world.createEntity();
		PooledObjectPosition c = EntityHelper.edit(world, e).create(PooledObjectPosition.class);
		assertEquals(0, c.vec2.x, 0.0001f);
		assertEquals(0, c.vec2.x, 0.0001f);
		
		c.vec2.x = 2;
		c.vec2.y = 3;
		
		world.process();
		world.deleteEntity(e);
		world.process();
		
		int e2 = world.createEntity();
		PooledObjectPosition c2 = EntityHelper.edit(world, e2).create(PooledObjectPosition.class);
		assertEquals(2, c.vec2.x, 0.0001f);
		assertEquals(3, c.vec2.y, 0.0001f);
		
		assertTrue(c == c2);
	}
	
	private static PooledPosition createPosition(World world, int e) {
		PooledPosition c = EntityHelper.edit(world, e).create(PooledPosition.class);
		assertEquals(0, c.x, 0.0001f);
		assertEquals(0, c.y, 0.0001f);
		c.x = 2;
		c.y = 2;
		

		return c;
	}
}
