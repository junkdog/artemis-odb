package com.artemis.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.MundaneWireException;
import com.artemis.World;

@SuppressWarnings("static-method")
public class UuidEntityManagerTest {
	
	@Test(expected=MundaneWireException.class)
	public void throw_exception_missing_uuid_manager() {
		World world = new World();
		world.initialize();
		
		Entity entity = world.createEntity();
		
		Assert.assertNotNull(entity.getUuid());
	}
	
	@Test
	public void uuid_assigned() {
		World world = new World();
		world.setManager(new UuidEntityManager());
		world.initialize();
		
		Entity entity = world.createEntity();
		
		assertNotNull(entity.getUuid());
		UUID uuid1 = entity.getUuid();
		world.deleteEntity(entity);
		
		world.process();
		world.process();
		
		entity = world.createEntity();
		
		assertNotNull(entity.getUuid());
		UUID uuid2 = entity.getUuid();

		assertNotEquals(uuid1, uuid2);
	}
	
	@Test
	public void uuid_updates_work() {
		World world = new World();
		UuidEntityManager uuidManager = world.setManager(new UuidEntityManager());
		world.initialize();
		
		Entity entity = world.createEntity();
		
		UUID uuid0 = entity.getUuid();
		Assert.assertNotNull(uuid0);
		
		UUID uuid1 = UUID.randomUUID();
		
		assertEquals(uuid0, entity.getUuid());
		entity.setUuid(uuid1);
		assertEquals(uuid1, entity.getUuid());
		
		
		assertNotEquals(uuid0, uuid1);
		assertNull(uuidManager.getEntity(uuid0));
		assertEquals(entity, uuidManager.getEntity(uuid1));
	}
	
	@Test
	public void explicit_uuids() {
		World world = new World();
		world.setManager(new UuidEntityManager());
		world.initialize();
		
		UUID[] uuids = new UUID[3];
		uuids[0] = UUID.randomUUID();
		uuids[1] = UUID.randomUUID();
		uuids[2] = UUID.randomUUID();
		
		Entity e1 = world.createEntity(uuids[0]);
		Entity e2 = world.createEntity(uuids[1]);
		Entity e3 = world.createEntity(uuids[2]);
		
		assertEquals(uuids[0], e1.getUuid());
		assertEquals(uuids[1], e2.getUuid());
		assertEquals(uuids[2], e3.getUuid());
	}
}
