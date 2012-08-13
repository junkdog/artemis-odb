package com.artemis;

import com.artemis.utils.Bag;

public class EntityManager extends Manager {
	private Bag<Entity> entities;
	
	private int active;
	private long added;
	private long created;
	private long deleted;

	private IdentifierPool identifierPool;
	
	public EntityManager() {
		entities = new Bag<Entity>();
		identifierPool = new IdentifierPool();
	}
	
	@Override
	protected void initialize() {
	}

	protected Entity createEntityInstance() {
		Entity e = new Entity(world, identifierPool.checkOut());
		active++;
		created++;
		return e;
	}
	
	@Override
	protected void added(Entity e) {
		active++;
		added++;
		entities.set(e.getId(), e);
	}
	
	@Override
	protected void changed(Entity e) {
	}

	@Override
	protected void deleted(Entity e) {
		entities.set(e.getId(), null);
		
		e.setTypeBits(0);

		identifierPool.checkIn(e.getId());
		
		active--;
		deleted++;
	}

	


	/**
	 * Check if this entity is active.
	 * Active means the entity is being actively processed.
	 * 
	 * @param entityId
	 * @return true if active, false if not.
	 */
	public boolean isActive(int entityId) {
		return entities.get(entityId) != null;
	}
	
	/**
	 * Get a entity with this id.
	 * 
	 * @param entityId
	 * @return the entity
	 */
	protected Entity getEntity(int entityId) {
		return entities.get(entityId);
	}
	
	/**
	 * Get how many entities are active in this world.
	 * @return how many entities are currently active.
	 */
	public int getActiveEntityCount() {
		return active;
	}
	
	/**
	 * Get how many entities have been created in the world since start.
	 * Note: A created entity may not have been added to the world, thus
	 * created count is always equal or larger than added count.
	 * @return how many entities have been created since start.
	 */
	public long getTotalCreated() {
		return created;
	}
	
	/**
	 * Get how many entities have been added to the world since start.
	 * @return how many entities have been added.
	 */
	public long getTotalAdded() {
		return added;
	}
	
	/**
	 * Get how many entities have been deleted from the world since start.
	 * @return how many entities have been deleted since start.
	 */
	public long getTotalDeleted() {
		return deleted;
	}
	
	
	
	/*
	 * Used only internally to generate distinct ids for entities and reuse them.
	 */
	private class IdentifierPool {
		private Bag<Integer> ids;
		private int nextAvailableId;

		public IdentifierPool() {
			ids = new Bag<Integer>();
		}
		
		public int checkOut() {
			if(ids.size() > 0) {
				return ids.removeLast();
			}
			return nextAvailableId++;
		}
		
		public void checkIn(int id) {
			ids.add(id);
		}
	}
}
