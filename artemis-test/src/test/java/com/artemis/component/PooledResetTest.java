package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.PooledComponent;
import com.artemis.World;

public class PooledResetTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
	}
	
	@Test
	public void test_primitive_fields_are_reset() {
		assertEquals(PooledComponent.class, PooledPosition.class.getSuperclass());
		
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
		PooledObjectPosition c = e.createComponent(PooledObjectPosition.class);
		assertEquals(0, c.vec2.x, 0.0001f);
		assertEquals(0, c.vec2.x, 0.0001f);
		
		c.vec2.x = 2;
		c.vec2.y = 3;
		
		world.process();
		e.deleteFromWorld();
		world.process();
		
		Entity e2 = world.createEntity();
		PooledObjectPosition c2 = e2.createComponent(PooledObjectPosition.class);
		assertEquals(2, c.vec2.x, 0.0001f);
		assertEquals(3, c.vec2.y, 0.0001f);
		
		assertTrue(c == c2);
	}
	
	private static PooledPosition createPosition(Entity e) {
		PooledPosition c = e.createComponent(PooledPosition.class);
		assertEquals(0, c.x, 0.0001f);
		assertEquals(0, c.y, 0.0001f);
		c.x = 2;
		c.y = 2;
		
		e.addToWorld();
		
		return c;
	}
}
