package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class EntityManagerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
	}
	
	@Test
	public void old_entities_are_recycled() {
		Set<Integer> ids = new HashSet<Integer>();
		
		Entity e1 = world.createEntity();
		e1.addToWorld();
		Entity e2 = world.createEntity();
		e2.addToWorld();
		Entity e3 = world.createEntity();
		e3.addToWorld();
		
		ids.add(System.identityHashCode(e1));
		ids.add(System.identityHashCode(e2));
		ids.add(System.identityHashCode(e3));
		
		assertEquals(3, ids.size());
		
		e1.deleteFromWorld();
		e2.deleteFromWorld();
		e3.deleteFromWorld();
		
		Entity e1b = world.createEntity();
		e1b.addToWorld();
		Entity e2b = world.createEntity();
		e2b.addToWorld();
		Entity e3b = world.createEntity();
		e3b.addToWorld();

		ids.add(System.identityHashCode(e1b));
		ids.add(System.identityHashCode(e2b));
		ids.add(System.identityHashCode(e3b));
		
		assertEquals(3, ids.size());
	}
}
