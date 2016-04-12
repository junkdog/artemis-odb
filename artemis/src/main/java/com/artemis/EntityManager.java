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
	/** Stores the bits of all currently disabled entities IDs. */
	private final RecyclingEntityFactory recyclingEntityFactory;

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
		recyclingEntityFactory = new RecyclingEntityFactory(this);
	}

	@Override
	protected void processSystem() {}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance() {
		return recyclingEntityFactory.obtain();
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity id
	 */
	protected int create() {
		return recyclingEntityFactory.obtain().id;
	}

	void clean(IntBag pendingDeletion) {
		int[] ids = pendingDeletion.getData();
		for(int i = 0, s = pendingDeletion.size(); s > i; i++) {
			int entityId = ids[i];
			// usually never happens but:
			// this happens when an entity is deleted before
			// it is added to the world, ie; created and deleted
			// before World#process has been called
			if (!recyclingEntityFactory.has(entityId)) {
				recyclingEntityFactory.free(entityId);
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
		return !recyclingEntityFactory.has(entityId);
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
	protected Entity createEntity(int id) {
		Entity e = new Entity(world, id);
		if (e.id >= entities.getCapacity()) {
			int newSize = 2 * entities.getCapacity();
			entities.ensureCapacity(newSize);
			ComponentManager cm = world.getComponentManager();
			cm.ensureCapacity(newSize);
		}

		entities.fastSet(e.id, e);

		return e;
	}

	/** Track retired entities for recycling. */
	private static final class RecyclingEntityFactory {
		private final EntityManager em;
		private final IntDeque limbo;
		private final BitSet recycled;
		private int nextId;

		RecyclingEntityFactory(EntityManager em) {
			this.em = em;
			recycled = new BitSet();
			limbo = new IntDeque();
		}
		
		void free(int entityId) {
			limbo.add(entityId);
			recycled.set(entityId);
		}
		
		Entity obtain() {
			if (limbo.isEmpty()) {
				return em.createEntity(nextId++);
			} else {
				int id = limbo.popFirst();
				recycled.set(id, false);
				return em.entities.get(id);
			}
		}

		boolean has(int entityId) {
			return recycled.get(entityId);
		}
	}
}
