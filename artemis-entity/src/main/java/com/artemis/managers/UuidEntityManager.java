package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;

public class UuidEntityManager<T extends Entity> extends Manager<T> {
	private final Map<UUID, Entity> uuidToEntity;
	private final Bag<UUID> entityToUuid;

	public UuidEntityManager() {
		this.uuidToEntity = new HashMap<>();
		this.entityToUuid = new Bag<>();
	}

	@Override
	public void deleted(Entity e) {
		UUID uuid = entityToUuid.safeGet(e.getId());
		if (uuid == null)
			return;

		Entity oldEntity = uuidToEntity.get(uuid);
		if (oldEntity != null && oldEntity.equals(e))
			uuidToEntity.remove(uuid);

		entityToUuid.set(e.getId(), null);
	}
	
	public void updatedUuid(Entity e, UUID newUuid) {
		setUuid(e, newUuid);
	}
	
	public Entity getEntity(UUID uuid) {
		return uuidToEntity.get(uuid);
	}

	public UUID getUuid(Entity e) {
		UUID uuid = entityToUuid.safeGet(e.getId());
		if (uuid == null) {
			uuid = UUID.randomUUID();
			setUuid(e, uuid);
		}
		
		return uuid;
	}

	public void setUuid(int id, UUID newUuid) {
		setUuid(world.getEntity(id), newUuid);
	}

	public void setUuid(Entity e, UUID newUuid) {
		UUID oldUuid = entityToUuid.safeGet(e.getId());
		if (oldUuid != null)
			uuidToEntity.remove(oldUuid);
		
		uuidToEntity.put(newUuid, e);
		entityToUuid.set(e.getId(), newUuid);
	}
}
