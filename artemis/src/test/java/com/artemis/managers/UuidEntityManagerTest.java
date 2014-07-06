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
	public void deleted_entities_should_be_removed() {
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
}
