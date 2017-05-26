package com.artemis.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.artemis.WorldConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.EntityWorld;

public class GroupManagerTest {
	private static final String GROUPIE = "groupie";
	private static final String GROUPIE2 = "groupie2";
	
	private EntityWorld world;
	private GroupManager gm;

	@Before
	public void setUp() throws Exception {
		gm = new GroupManager();
		world = new EntityWorld(new WorldConfiguration()
				.setSystem(gm));
		world.inject(this);
	}
	
	@Test
	public void added_entities_should_only_occur_once() {
		Entity entity = world.createEntity();
		gm.add(entity, GROUPIE);
		gm.add(entity, GROUPIE);

		assertEquals(1, gm.getEntityIds(GROUPIE).size());
	}
	
	@Test
	public void deleted_entities_should_be_removed() {
		Entity entity = world.createEntity();
		gm.add(entity, GROUPIE);

		assertEquals(1, gm.getEntityIds(GROUPIE).size());

		entity.deleteFromWorld();
		world.process();
		assertEquals(0, gm.getEntityIds(GROUPIE).size());
		assertFalse(gm.isInAnyGroup(entity));
	}
	
	@Test
	public void deleted_entities_should_be_removed_from_all_groups() {
		Entity entity = world.createEntity();
		gm.add(entity, GROUPIE);
		gm.add(entity, GROUPIE2);
		
		assertEquals(1, gm.getEntityIds(GROUPIE).size());
		assertEquals(1, gm.getEntityIds(GROUPIE2).size());
		
		entity.deleteFromWorld();
		world.process();
		
		assertEquals(0, gm.getEntityIds(GROUPIE).size());
		assertEquals(0, gm.getEntityIds(GROUPIE2).size());
		assertFalse(gm.isInAnyGroup(entity));
	}
}
