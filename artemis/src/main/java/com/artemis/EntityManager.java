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
	private final BitSet newlyCreatedEntityIds;
	/** Stores the bits of all currently disabled entities IDs. */
	private final BitSet disabled;
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
		newlyCreatedEntityIds = new BitSet();
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

		// growing backing array just in case
		entities.set(e.getId(), e);
		newlyCreatedEntityIds.set(e.id);
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
	BitSet componentBits(int entityId) {
		int identityIndex = entityToIdentity.get(entityId);
		if (identityIndex == 0)
			identityIndex = forceResolveIdentity(entityId);
		
		return identityResolver.composition.get(identityIndex);
	}

	/**
	 * Sets the entity (re)enabled in the manager.
	 *
	 * @param entityId
	 *			the entity to (re)enable
	 * @deprecated create your own components to track state.
	 */
	@Override @Deprecated
	public void enabled(int entityId) {
		disabled.clear(entityId);
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
	 * @param entityId
	 *			the entity to disable
	 */
	@Override @Deprecated
	public void disabled(int entityId) {
		disabled.set(entityId);
	}

	/**
	 * Removes the entity from the manager, freeing it's id for new entities.
	 *
	 * @param entityId
	 *			the entity to remove
	 */
	@Override
	public void deleted(int entityId) {
		Entity entity = entities.get(entityId);
		if (entity == null)
			return;

		entities.set(entityId, null);

		// usually never happens but:
		// this happens when an entity is deleted before
		// it is added to the world, ie; created and deleted
		// before World#process has been called
		newlyCreatedEntityIds.set(entityId, false);

		recyclingEntityFactory.free(entity);

		disabled.clear(entityId);
	}

	@Override
	public void added(int entityId) {
		newlyCreatedEntityIds.set(entityId, false);
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
		return (entities.size() > entityId) && !newlyCreatedEntityIds.get(entityId);
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
	 * @deprecated Implement your own manager if you need this functionality.
	 *             This implementation is stubbed to always return {@code 0}.
	 */
	@Deprecated
	public int getActiveEntityCount() {
		return 0;
	}
	
	/**
	 * Get how many entities have been created in the world since start.
	 * <p>
	 * Note: A created entity may not have been added to the world, thus
	 * created count is always equal or larger than added count.
	 * </p>
	 *
	 * @return how many entities have been created since start
	 * @deprecated Implement your own manager if you need this functionality.
	 *             This implementation is stubbed to always return {@code 0}.
	 */
	@Deprecated
	public long getTotalCreated() {
		return 0L;
	}
	
	/**
	 * Get how many entities have been added to the world since start.
	 *
	 * @return how many entities have been added
	 * @deprecated Implement your own manager if you need this functionality.
	 *             This implementation is stubbed to always return {@code 0}.
	 */
	@Deprecated
	public long getTotalAdded() {
		return 0L;
	}
	
	/**
	 * Get how many entities have been deleted from the world since start.
	 *
	 * @return how many entities have been deleted since start
	 * @deprecated Implement your own manager if you need this functionality.
	 *             This implementation is stubbed to always return {@code 0}.
	 */
	@Deprecated
	public long getTotalDeleted() {
		return 0L;
	}
	
	protected void clean() {
		recyclingEntityFactory.recycle();
	}
	
	protected int getIdentity(int entityId) {
		int identity = entityToIdentity.get(entityId);
		if (identity == 0)
			identity = forceResolveIdentity(entityId);

		return identity;
	}

	void setIdentity(Entity e, TransmuteOperation operation) {
		entityToIdentity.set(e.getId(), operation.compositionId);
	}

	private int forceResolveIdentity(int entityId) {
		updateCompositionIdentity(entities.get(entityId).edit());
		return entityToIdentity.get(entityId);
	}

	void synchronize(EntitySubscription es) {
		for (int i = 1; highestSeenIdentity >= i; i++) {
			BitSet componentBits = identityResolver.composition.get(i);
			es.processComponentIdentity(i, componentBits);
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e != null && !disabled.get(e.id))
				es.check(e.id);
		}

		es.informEntityChanges();
		es.rebuildCompressedActives();
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
