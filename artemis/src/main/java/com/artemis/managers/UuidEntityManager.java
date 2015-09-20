package com.artemis.managers;

import java.util.UUID;

import com.artemis.EntityHelper;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.ObjectIntMap;

public class UuidEntityManager extends Manager {
	private final ObjectIntMap<UUID> uuidToEntity;
	private final Bag<UUID> entityToUuid;

	private int flyweight;

	public UuidEntityManager() {
		this.uuidToEntity = new ObjectIntMap<UUID>();
		this.entityToUuid = new Bag<UUID>();
	}

	@Override
	protected void initialize() {
		flyweight = world.getEntityManager()
				.createFlyweight();
	}

	@Override
	public void deleted(int entityId) {
		UUID uuid = entityToUuid.safeGet(entityId);
		if (uuid == null)
			return;

		int oldEntity = uuidToEntity.get(uuid, EntityHelper.NO_ENTITY);
		if (oldEntity != EntityHelper.NO_ENTITY && oldEntity == entityId) {
			uuidToEntity.remove(uuid, EntityHelper.NO_ENTITY);
		}

		entityToUuid.set(entityId, null);
	}
	
	public void updatedUuid(int e, UUID newUuid) {
		setUuid(e, newUuid);
	}
	
	public int getEntity(UUID uuid) {
		return uuidToEntity.get(uuid, EntityHelper.NO_ENTITY);
	}

	public UUID getUuid(int e) {
		UUID uuid = entityToUuid.safeGet(e);
		if (uuid == null) {
			uuid = UUID.randomUUID();
			setUuid(e, uuid);
		}
		
		return uuid;
	}
	
	public void setUuid(int e, UUID newUuid) {
		UUID oldUuid = entityToUuid.safeGet(e);
		if (oldUuid != null)
			uuidToEntity.remove(oldUuid, EntityHelper.NO_ENTITY);
		
		uuidToEntity.put(newUuid, e);
		entityToUuid.set(e, newUuid);
	}
}
