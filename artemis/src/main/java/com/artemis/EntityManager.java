package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.EntityTransmuter.TransmuteOperation;

import java.util.BitSet;


/**
 * EntityManager.
 *
 * @author Arni Arent
 */
public class EntityManager extends Manager {

	static final int NO_COMPONENTS = 1;
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
	
	ComponentIdentityResolver identityResolver = new ComponentIdentityResolver();
	private IntBag entityToIdentity = new IntBag();
	private int highestSeenIdentity;
	private AspectSubscriptionManager subscriptionManager;

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
		disabled = new BitSet();
	}
	
	@Override
	protected void initialize() {
		recyclingEntityFactory = new RecyclingEntityFactory(world, entityToIdentity);
		subscriptionManager = world.getManager(AspectSubscriptionManager.class);
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance() {
		Entity e = recyclingEntityFactory.obtain();
		entityToIdentity.set(e.getId(), 0);
		created++;

		// growing backing array just in case
		entities.set(e.getId(), null);
		return e;
	}
	
	/**
	 * Create a new entity based on the supplied archetype.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance(Archetype archetype) {
		Entity e = createEntityInstance();
		entityToIdentity.set(e.getId(), archetype.compositionId);
		return e;
	}

	/** Get component composition of entity. */
	BitSet componentBits(Entity e) {
		int identityIndex = entityToIdentity.get(e.getId());
		if (identityIndex == 0)
			identityIndex = forceResolveIdentity(e);
		
		return identityResolver.composition.get(identityIndex);
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
	 * @deprecated create your own components to track state.
	 */
	@Override
	@Deprecated
	public void enabled(Entity e) {
		disabled.clear(e.getId());
	}

	/** Refresh entity composition identity if it changed. */
	void updateCompositionIdentity(EntityEdit edit) {
		int identity = compositionIdentity(edit.componentBits);
		entityToIdentity.set(edit.entity.getId(), identity);
	}

	/**
	 * Fetches unique identifier for composition.
	 *
	 * @param componentBits composition to fetch unique identifier for.
	 * @return Unique identifier for passed composition.
	 */
	int compositionIdentity(BitSet componentBits) {
		int identity = identityResolver.getIdentity(componentBits);
		if (identity > highestSeenIdentity) {
			subscriptionManager.processComponentIdentity(identity, componentBits);
			highestSeenIdentity = identity;
		}
		return identity;
	}
	
	/**
	 * Sets the entity as disabled in the manager.
	 *
	 * @param e
	 *			the entity to disable
	 */
	@Override @Deprecated
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
		if (entities.get(e.getId()) != null) {
			entities.set(e.getId(), null);
			active--;
		}
		disabled.clear(e.getId());
		
		recyclingEntityFactory.free(e);
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
		return (entities.size() > entityId) ? entities.get(entityId) != null : false; 
	}
	
	/**
	 * Check if the specified entityId is enabled.
	 * 
	 * @param entityId
	 *			the entities id
	 *
	 * @return true if the entity is enabled, false if it is disabled
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
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
	
	protected int getIdentity(Entity e) {
		int identity = entityToIdentity.get(e.getId());
		if (identity == 0)
			identity = forceResolveIdentity(e);

		return identity;
	}

	void setIdentity(Entity e, TransmuteOperation operation) {
		entityToIdentity.set(e.getId(), operation.compositionId);
	}

	private int forceResolveIdentity(Entity e) {
		updateCompositionIdentity(e.edit());
		return entityToIdentity.get(e.getId());
	}

	/** Tracks all unique component compositions. */
	private static final class ComponentIdentityResolver {
		private final Bag<BitSet> composition;
		
		ComponentIdentityResolver() {
			composition = new Bag<BitSet>();
			composition.add(null);
			composition.add(new BitSet());
		}

		/** Fetch unique identity for passed composition. */
		int getIdentity(BitSet components) {
			Object[] bitsets = composition.getData();
			int size = composition.size();
			for (int i = NO_COMPONENTS; size > i; i++) { // want to start from 1 so that 0 can mean null
				if (components.equals(bitsets[i]))
					return i;
			}
			composition.add((BitSet)components.clone());
			return size;
		}
	}
	
	private static final class RecyclingEntityFactory {
		private final World world;
		private final WildBag<Entity> limbo;
		private final Bag<Entity> recycled;
		private int nextId;
		private IntBag entityToIdentity;
		
		RecyclingEntityFactory(World world, IntBag entityToIdentity) {
			this.world = world;
			this.entityToIdentity = entityToIdentity;
			recycled = new Bag<Entity>();
			limbo = new WildBag<Entity>();
		}
		
		void free(Entity e) {
			limbo.add(e);
		}
		
		void recycle() {
			int s = limbo.size();
			if (s == 0) return;
			
			Object[] data = limbo.getData();
			for (int i = 0; s > i; i++) {
				Entity e = (Entity) data[i];
				recycled.add(e);
				data[i] = null;
			}
			limbo.setSize(0);
		}
		
		Entity obtain() {
			if (recycled.isEmpty()) {
				return new Entity(world, nextId++);
			} else {
				Entity e = recycled.removeLast();
				entityToIdentity.set(e.getId(), 0);
				return e;
			}
		}
	}
}
