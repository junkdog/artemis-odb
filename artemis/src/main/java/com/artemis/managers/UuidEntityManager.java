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

	@Override
	public void deleted(Entity e) {
		UUID uuid = entityToUuid.safeGet(e.getId());
		if (uuid == null)
			return;
		
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
	
	public void setUuid(Entity e, UUID newUuid) {
		UUID oldUuid = entityToUuid.safeGet(e.getId());
		if (oldUuid != null)
			uuidToEntity.remove(oldUuid);
		
		uuidToEntity.put(newUuid, e);
		entityToUuid.set(e.getId(), newUuid);
	}
}
