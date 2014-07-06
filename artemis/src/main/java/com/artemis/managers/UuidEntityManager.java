package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;

public class UuidEntityManager extends Manager {
	private final Map<UUID, Entity> uuidToEntity;
	private final Bag<UUID> entityToUuid;

	public UuidEntityManager() {
		this.uuidToEntity = new HashMap<UUID, Entity>();
		this.entityToUuid = new Bag<UUID>();
	}

	/**
	 * Method is automatically called when adding an Entity to the world.
	 */
	public void add(Entity e) {
		UUID uuid = UUID.randomUUID();
		setUuid(e, uuid);
	}

	@Override
	public void deleted(Entity e) {
		uuidToEntity.remove(e.getUuid());
		entityToUuid.set(e.getId(), null);
	}
	
	public void updatedUuid(Entity e, UUID newUuid) {
		UUID oldUuid = entityToUuid.get(e.getId());
		uuidToEntity.remove(oldUuid);
		
		setUuid(e, newUuid);
	}
	
	public Entity getEntity(UUID uuid) {
		return uuidToEntity.get(uuid);
	}

	public UUID getUuid(Entity e) {
		return entityToUuid.get(e.getId());
	}
	
	private void setUuid(Entity e, UUID newUuid) {
		uuidToEntity.put(newUuid, e);
		entityToUuid.set(e.getId(), newUuid);
	}
}
