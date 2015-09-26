package com.artemis.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import com.artemis.WorldConfiguration;
import org.junit.Assert;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;

@SuppressWarnings("static-method")
public class UuidEntityManagerTest {

	@Test
	public void uuid_updates_work() {
		UuidEntityManager uuidManager = new UuidEntityManager();
		World world = new World(new WorldConfiguration()
				.setSystem(uuidManager));

		Entity entity = world.createEntity();
		
		UUID uuid0 = uuidManager.getUuid(entity);
		Assert.assertNotNull(uuid0);
		
		UUID uuid1 = UUID.randomUUID();
		
		assertEquals(uuid0, uuidManager.getUuid(entity));
		uuidManager.setUuid(entity,uuid1);
		assertEquals(uuid1, uuidManager.getUuid(entity));
		
		
		assertNotEquals(uuid0, uuid1);
		assertNull(uuidManager.getEntity(uuid0));
		assertEquals(entity, uuidManager.getEntity(uuid1));
	}

	@Test
	public void reuser_uuids_during_same_tick() {
		UUID uuid = UUID.randomUUID();

		WorldConfiguration configuration = new WorldConfiguration();
        configuration.setSystem(UuidEntityManager.class);

	    World world = new World(configuration);
	    UuidEntityManager uuidEntityManager = world.getSystem(UuidEntityManager.class);
	    Entity entity = world.createEntity();
		uuidEntityManager.setUuid(entity, uuid);
	    world.process();
		assertEquals(0, uuidEntityManager.getEntity(uuid).getId()); // Entity[0]
		world.deleteEntity(entity);
		entity = world.createEntity();
		uuidEntityManager.setUuid(entity, uuid);
	    assertEquals(1, uuidEntityManager.getEntity(uuid).getId()); // Entity[1]
	    world.process();
	    assertEquals(1, uuidEntityManager.getEntity(uuid).getId());
	}
}
