package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.IntDeque;

import java.util.BitSet;

/**
 * Manages entity instances.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
@SkipWire
public class EntityManager extends BaseSystem {
	/** Contains all entities in the manager. */
	final Bag<Entity> entities;
	private final BitSet recycled = new BitSet();
	private final IntDeque limbo = new IntDeque();
	private int nextId;

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
	}

	@Override
	protected void processSystem() {}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance() {
		return obtain();
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity id
	 */
	protected int create() {
		return obtain().id;
	}

	void clean(IntBag pendingDeletion) {
		int[] ids = pendingDeletion.getData();
		for(int i = 0, s = pendingDeletion.size(); s > i; i++) {
			int entityId = ids[i];
			// usually never happens but:
			// this happens when an entity is deleted before
			// it is added to the world, ie; created and deleted
			// before World#process has been called
			if (!recycled.get(entityId)) {
				free(entityId);
			}
		}
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
		return !recycled.get(entityId);
	}

	/**
	 * Resolves entity id to the unique entity instance. <em>This method may
	 * return an entity even if it isn't active in the world, </em> use
	 * {@link #isActive(int)} if you need to check whether the entity is active or not.
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
	 * Instantiates an Entity without registering it into the world.
	 * @param id The ID to be set on the Entity
	 */
	private Entity createEntity(int id) {
		Entity e = new Entity(world, id);
		if (e.id >= entities.getCapacity()) {
			int newSize = 2 * entities.getCapacity();
			entities.ensureCapacity(newSize);
			ComponentManager cm = world.getComponentManager();
			cm.ensureCapacity(newSize);
		}

		// can't use unsafe set, as we need to track highest id
		// for faster iteration when syncing up new subscriptions
		// in ComponentManager#synchronize
		entities.set(e.id, e);

		return e;
	}

	private Entity obtain() {
		if (limbo.isEmpty()) {
			return createEntity(nextId++);
		} else {
			int id = limbo.popFirst();
			recycled.set(id, false);
			return entities.get(id);
		}
	}

	private void free(int entityId) {
		limbo.add(entityId);
		recycled.set(entityId);
	}
}
