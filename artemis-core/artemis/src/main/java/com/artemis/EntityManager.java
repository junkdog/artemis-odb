package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.IntDeque;

import com.artemis.utils.BitVector;

import static com.artemis.Aspect.all;

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
	private final BitVector recycled = new BitVector();
	private final IntDeque limbo = new IntDeque();
	private int nextId;
	private Bag<BitVector> entityBitVectors = new Bag<BitVector>(BitVector.class);

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
		registerEntityStore(recycled);
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
			int id = ids[i];
			// usually never happens but:
			// this happens when an entity is deleted before
			// it is added to the world, ie; created and deleted
			// before World#process has been called
			if (!recycled.unsafeGet(id)) {
				free(id);
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
		return !recycled.unsafeGet(entityId);
	}

	public void registerEntityStore(BitVector bv) {
		bv.ensureCapacity(entities.getCapacity());
		entityBitVectors.add(bv);
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
	 * <p>If all entties have been deleted, resets the entity cache - with next entity
	 * entity receiving id <code>0</code>. There mustn't be any active entities in
	 * the world for this method to work. This method does nothing if it fails.</p>
	 *
	 * <p>For the reset to take effect, a new {@link World#process()} must initiate.</p>
	 *
	 * @return true if entity id was successfully reset.
	 *
	 */
	public boolean reset() {
		int count = world.getAspectSubscriptionManager()
			.get(all())
			.getActiveEntityIds()
			.cardinality();

		if (count > 0)
			return false;

		limbo.clear();
		recycled.clear();
		entities.clear();

		nextId = 0;

		return true;
	}

	/**
	 * Instantiates an Entity without registering it into the world.
	 * @param id The ID to be set on the Entity
	 */
	private Entity createEntity(int id) {
		Entity e = new Entity(world, id);
		if (e.id >= entities.getCapacity()) {
			growEntityStores();
		}

		// can't use unsafe set, as we need to track highest id
		// for faster iteration when syncing up new subscriptions
		// in ComponentManager#synchronize
		entities.set(e.id, e);

		return e;
	}

	private void growEntityStores() {
		int newSize = 2 * entities.getCapacity();
		entities.ensureCapacity(newSize);
		ComponentManager cm = world.getComponentManager();
		cm.ensureCapacity(newSize);

		for (int i = 0, s = entityBitVectors.size(); s > i; i++) {
			entityBitVectors.get(i).ensureCapacity(newSize);
		}
	}

	private Entity obtain() {
		if (limbo.isEmpty()) {
			return createEntity(nextId++);
		} else {
			int id = limbo.popFirst();
			recycled.unsafeClear(id);
			return entities.get(id);
		}
	}

	private void free(int entityId) {
		limbo.add(entityId);
		recycled.unsafeSet(entityId);
	}
}
