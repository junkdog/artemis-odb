package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;


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
	private RecyclingEntityFactory recyclingEntityFactory;

	/**
	 * Creates a new EntityManager Instance.
	 */
	public EntityManager() {
		entities = new Bag<Entity>();
		disabled = new BitSet();
	}
	@Override
	protected void initialize() {
		recyclingEntityFactory = new RecyclingEntityFactory(world);
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
		Entity e = recyclingEntityFactory.obtain();
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
		
		recyclingEntityFactory.free(e);
		
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
	
	protected void clean() {
		recyclingEntityFactory.recycle();
	}
	
	private static final class RecyclingEntityFactory {
		private final World world;
		private final Bag<Entity> limbo;
		private final Bag<Entity> recycled;
		private int nextId;
		
		RecyclingEntityFactory(World world) {
			this.world = world;
			recycled = new Bag<Entity>();
			limbo = new Bag<Entity>();
		}
		
		void free(Entity e) {
			limbo.add(e);
		}
		
		void recycle() {
			int s = limbo.size();
			if (s == 0)
				return;
			
			Object[] data = limbo.getData();
			for (int i = 0; s > i; i++) {
				recycled.add((Entity)data[i]);
			}
		}
		
		Entity obtain() {
			if (recycled.isEmpty()) {
				return new Entity(world, nextId++);
			} else {
				Entity entity = recycled.removeLast();
				entity.reset();
				return entity; 
			}
		}
	}
}
