package com.artemis.managers;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.MundaneWireException;
import com.artemis.World;

public class UuidEntityManagerTest {
	
	@Test(expected=MundaneWireException.class)
	public void throw_exception_missing_uuid_manager() {
		World world = new World();
		world.initialize();
		
		Entity entity = world.createEntity();
		entity.addToWorld();
		
		Assert.assertNotNull(entity.getUuid());
	}
	
	@Test
	public void uuid_assigned() {
		World world = new World();
		world.setManager(new UuidEntityManager());
		world.initialize();
		
		Entity entity = world.createEntity();
		entity.addToWorld();
		
		Assert.assertNotNull(entity.getUuid());
		UUID uuid1 = entity.getUuid();
		world.deleteEntity(entity);
		
		world.process();
		world.process();
		
		entity = world.createEntity();
		entity.addToWorld();
		
		Assert.assertNotNull(entity.getUuid());
		UUID uuid2 = entity.getUuid();

		Assert.assertNotEquals(uuid1, uuid2);
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
		e1.addToWorld();
		Entity e2 = world.createEntity(uuids[1]);
		e2.addToWorld();
		Entity e3 = world.createEntity(uuids[2]);
		e3.addToWorld();
		
		Assert.assertEquals(uuids[0], e1.getUuid());
		Assert.assertEquals(uuids[1], e2.getUuid());
		Assert.assertEquals(uuids[2], e3.getUuid());
	}
}
