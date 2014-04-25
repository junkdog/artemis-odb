package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.artemis.Entity;
import com.artemis.Manager;

public class UuidEntityManager extends Manager {
	private final Map<UUID, Entity> entities;

	public UuidEntityManager() {
		this.entities = new HashMap<UUID,Entity>();
	}

	@Override
	public void added(Entity e) {
		entities.put(e.getUuid(), e);
	}

	@Override
	public void deleted(Entity e) {
		entities.remove(e.getUuid());
	}
	
	public Entity getEntity(UUID uuid) {
		return entities.get(uuid);
	}
}
