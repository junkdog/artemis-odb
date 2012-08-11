package com.artemis;

import com.artemis.utils.Bag;

public class EntityManager extends Manager {
	private Bag<Entity> entities;
	private int nextAvailableId;
	
	private int count;
	private long created;
	private long deleted;
	
	public EntityManager() {
		entities = new Bag<Entity>();
	}
	
	@Override
	protected void initialize() {
	}

	protected Entity createEntityInstance() {
		Entity e = new Entity(world, nextAvailableId++);
		count++;
		created++;
		return e;
	}
	
	@Override
	protected void added(Entity e) {
		count++;
		entities.set(e.getId(), e);
	}
	
	@Override
	protected void changed(Entity e) {
	}

	@Override
	protected void deleted(Entity e) {
		entities.set(e.getId(), null);
		
		e.setTypeBits(0);
		
		count--;
		deleted++;
	}


	
	


	/**
	 * Check if this entity is active, or has been deleted, within the framework.
	 * 
	 * @param entityId
	 * @return active or not.
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
	 * Get how many entities are in this world.
	 * @return how many entities are currently active.
	 */
	public int getEntityCount() {
		return count;
	}
	
	/**
	 * Get how many entities have been created in this world.
	 * @return how many entities have been created since start.
	 */
	public long getTotalCreated() {
		return created;
	}
	
	/**
	 * Get how many entities have been deleted in this world.
	 * @return how many entities have been deleted since start.
	 */
	public long getTotalRemoved() {
		return deleted;
	}
}
