package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;
import java.util.UUID;


/**
 * EntityManager.
 *
 * @author Arni Arent
 */
public class EntityManager extends Manager {

	/** Contains all entities in the manager. */
	private final Bag<Entity> entities;
	/** Stores the bits of all currently disabled entities IDs. */
	private final BitSet disabled;
	/** Amount of currently active (added to the world) entities. */
	private int active;
	/** Amount of entities ever added to the manager. */
	private long added;
	/** Amount of entites ever created by the manager. */
	private long created;
	/** Amount of entities ever deleted from the manager. */
	private long deleted;
	/** Manages free IDs for entities. */
	private final IdentifierPool identifierPool;


	/**
	 * Creates a new EntityManager Instance.
	 */
	public EntityManager() {
		entities = new Bag<Entity>();
		disabled = new BitSet();
		identifierPool = new IdentifierPool();
	}



	@Override
	protected void initialize() {
	}

	/**
	 * Create a new entity.
	 * <p>
	 * New entities will recieve a free ID from a global pool, ensuring
	 * every entity has a unique ID. Deleted entities free their ID for new
	 * entities.
	 * </p>
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance() {
		Entity e = new Entity(world, identifierPool.checkOut());
		created++;
		return e;
	}
	
	/**
	 * Create a new entity.
	 * <p>
	 * New entities will recieve a free ID from a global pool, ensuring
	 * every entity has a unique ID. Deleted entities free their ID for new
	 * entities.
	 * </p>
	 *
	 * @param uuid
	 *			the UUID to give to the entity
	 * 
	 * @return a new entity
	 */
	protected Entity createEntityInstance(UUID uuid) {
		Entity e = new Entity(world, identifierPool.checkOut(), uuid);
		created++;
		return e;
	}

	/**
	 * Adds the entity to this manager.
	 * <p>
	 * Called by the world when an entity is added.
	 * </p>
	 *
	 * @param e
	 *			the entity to add
	 */
	@Override
	public void added(Entity e) {
		active++;
		added++;
		entities.set(e.getId(), e);
	}

	/**
	 * Sets the entity (re)enabled in the manager.
	 *
	 * @param e
	 *			the entity to (re)enable
	 */
	@Override
	public void enabled(Entity e) {
		disabled.clear(e.getId());
	}

	/**
	 * Sets the entity as disabled in the manager.
	 *
	 * @param e
	 *			the entity to disable
	 */
	@Override
	public void disabled(Entity e) {
		disabled.set(e.getId());
	}

	/**
	 * Removes the entity from the manager, freeing it's id for new entities.
	 *
	 * @param e
	 *			the entity to remove
	 */
	@Override
	public void deleted(Entity e) {
		entities.set(e.getId(), null);
		
		disabled.clear(e.getId());
		
		identifierPool.checkIn(e.getId());
		
		active--;
		deleted++;
	}

	/**
	 * Check if this entity is active.
	 * <p>
	 * Active means the entity is being actively processed.
	 * </p>
	 * 
	 * @param entityId
	 *			the entities id
	 *
	 * @return true if active, false if not
	 */
	public boolean isActive(int entityId) {
		return entities.get(entityId) != null;
	}
	
	/**
	 * Check if the specified entityId is enabled.
	 * 
	 * @param entityId
	 *			the entities id
	 *
	 * @return true if the entity is enabled, false if it is disabled
	 */
	public boolean isEnabled(int entityId) {
		return !disabled.get(entityId);
	}
	
	/**
	 * Get a entity with this id.
	 * 
	 * @param entityId
	 *			the entities id
	 *
	 * @return the entity
	 */
	protected Entity getEntity(int entityId) {
		return entities.get(entityId);
	}
	
	/**
	 * Get how many entities are active in this world.
	 *
	 * @return how many entities are currently active
	 */
	public int getActiveEntityCount() {
		return active;
	}
	
	/**
	 * Get how many entities have been created in the world since start.
	 * <p>
	 * Note: A created entity may not have been added to the world, thus
	 * created count is always equal or larger than added count.
	 * </p>
	 *
	 * @return how many entities have been created since start
	 */
	public long getTotalCreated() {
		return created;
	}
	
	/**
	 * Get how many entities have been added to the world since start.
	 *
	 * @return how many entities have been added
	 */
	public long getTotalAdded() {
		return added;
	}
	
	/**
	 * Get how many entities have been deleted from the world since start.
	 *
	 * @return how many entities have been deleted since start
	 */
	public long getTotalDeleted() {
		return deleted;
	}
	
	
	
	/**
	 * Used only internally to generate distinct ids for entities and reuse
	 * them.
	 */
	private static final class IdentifierPool {
		
		/** Stores free, pre-used, IDs. */
		private DumbUnsafeIntArray ids;
		/** The next ID to be given out, if no free pre-used ones are available. */
		private int nextAvailableId;

		
		/** 
		 * Create a new identifier pool.
		 */
		public IdentifierPool() {
			ids = new DumbUnsafeIntArray();
		}

		
		/**
		 * Get a free id.
		 *
		 * @return a free id
		 */
		public int checkOut() {
			if(ids.size() > 0) {
				return ids.pop();
			}
			return nextAvailableId++;
		}

		/**
		 * Free the id.
		 *
		 * @param id
		 *			the id to free
		 */
		public void checkIn(int id) {
			ids.push(id);
		}
		
	}
	
	
	/**
	 * Used by the {@link IdentifierPool} too avoid boxing to {@code Integer}.
	 */
	private static class DumbUnsafeIntArray {
		
		/** The underlaying array. */
		private int[] items;
		/** The amount of items in the array. */
		private int size;
		
		
		/**
		 * Creates a new DumbUnsafeIntArray instance.
		 */
		public DumbUnsafeIntArray() {
			items = new int[64];
		}
		
		
		/**
		 * Append a value at the end of the array.
		 * 
		 * @param value 
		 *			the value to append
		 */
		public void push(int value) {
			items[size++] = value;
			if (size == items.length)
				grow();
		}
		
		/**
		 * Return and remove the last element from the array.
		 * 
		 * @return the last element in the array
		 */
		public int pop() {
			return items[--size];
		}
		
		/**
		 * Gets the size of the array.
		 * 
		 * @return the size
		 */
		public int size() {
			return size;
		}
		
		/**
		 * Creates a new, larger underlaying array and replaces the old one,
		 * copying all content to the new array.
		 */
		private void grow() {
			int[] old = items;
			items = new int[(old.length * 2)];
			System.arraycopy(old, 0, items, 0, size);
		}
		
	}
	
}
