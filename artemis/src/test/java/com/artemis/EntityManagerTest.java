package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.artemis.component.ComponentX;

public class EntityManagerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
	}
	
	@Test
	public void old_entities_are_recycled() {
		Set<Integer> ids = new HashSet<Integer>();
		
		Entity e1 = world.createEntity();
		Entity e2 = world.createEntity();
		Entity e3 = world.createEntity();
		
		ids.add(System.identityHashCode(e1));
		ids.add(System.identityHashCode(e2));
		ids.add(System.identityHashCode(e3));
		
		assertEquals(3, ids.size());
		
		e1.deleteFromWorld();
		e2.deleteFromWorld();
		e3.deleteFromWorld();
		
		world.process();
		
		Entity e1b = world.createEntity();
		Entity e2b = world.createEntity();
		Entity e3b = world.createEntity();

		ids.add(System.identityHashCode(e1b));
		ids.add(System.identityHashCode(e2b));
		ids.add(System.identityHashCode(e3b));
		
		assertEquals(3, ids.size());
	}
	
	@Test
	public void is_active_check_never_throws() {
		EntityManager em = world.getEntityManager();
		for (int i = 0; 1024 > i; i++) {
			Entity e = world.createEntity();
			assertFalse(em.isActive(e.getId()));
		}
	}
	
	@Test
	public void recycled_entities_behave_nicely_with_components() {
		ComponentMapper<ComponentX> mapper = world.getMapper(ComponentX.class);
		
		Entity e1 = world.createEntity();
		e1.edit().add(new ComponentX());
		assertTrue(mapper.has(e1));
		
		int id1 = e1.getId();
		e1.deleteFromWorld();
		
		Entity e2 = world.createEntity();
		
		assertNotEquals(id1, e2.getId());
		assertFalse("Error:" + mapper.getSafe(e2), mapper.has(e2));
	}
	
	@Test
	public void should_recycle_entities_after_one_round() {
		ComponentMapper<ComponentX> mapper = world.getMapper(ComponentX.class);
		
		Entity e1 = world.createEntity();
		e1.edit().add(new ComponentX());
		assertTrue(mapper.has(e1));
		
		int id1 = e1.getId();
		e1.deleteFromWorld();
		world.process();
		Entity e2 = world.createEntity();
		
		assertEquals(id1, e2.getId());
		assertFalse("Error:" + mapper.getSafe(e2), mapper.has(e2));
	}
}
