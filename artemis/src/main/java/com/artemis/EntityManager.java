package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.EntityTransmuter.TransmuteOperation;
import com.artemis.utils.IntDeque;

import java.util.BitSet;


/**
 * EntityManager.
 *
 * @author Arni Arent
 */
@SkipWire
public class EntityManager extends Manager {

	static final int NO_COMPONENTS = 1;
	/** Contains all entities in the manager. */
	private final BitSet entities;
	private final BitSet newlyCreatedEntityIds;
	/** Stores the bits of all currently disabled entities IDs. */
	private RecyclingEntityFactory recyclingEntityFactory;
	
	ComponentIdentityResolver identityResolver = new ComponentIdentityResolver();
	private IntBag entityToIdentity = new IntBag();
	private int highestSeenIdentity;
	private AspectSubscriptionManager subscriptionManager;

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new BitSet(initialContainerSize);
		newlyCreatedEntityIds = new BitSet();
	}
	
	@Override
	protected void initialize() {
		recyclingEntityFactory = new RecyclingEntityFactory(this);
		subscriptionManager = world.getSystem(AspectSubscriptionManager.class);
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity
	 */
	protected int createEntityInstance() {
		int e = recyclingEntityFactory.obtain();
		entityToIdentity.set(e, 0);

		newlyCreatedEntityIds.set(e);
		return e;
	}
	
	/**
	 * Create a new entity based on the supplied archetype.
	 *
	 * @return a new entity
	 */
	protected int createEntityInstance(Archetype archetype) {
		int e = createEntityInstance();
		entityToIdentity.set(e, archetype.compositionId);
		return e;
	}

	/** Get component composition of entity. */
	BitSet componentBits(int entityId) {
		int identityIndex = entityToIdentity.get(entityId);
		if (identityIndex == 0)
			identityIndex = forceResolveIdentity(entityId);
		
		return identityResolver.composition.get(identityIndex);
	}

	/** Refresh entity composition identity if it changed. */
	void updateCompositionIdentity(EntityEdit edit) {
		int identity = compositionIdentity(edit.componentBits);
		entityToIdentity.set(edit.entity, identity);
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
	 * Removes the entity from the manager, freeing it's id for new entities.
	 *
	 * @param entityId
	 *			the entity to remove
	 */
	@Override
	public void deleted(int entityId) {
		if (recyclingEntityFactory.has(entityId))
			return;

		// usually never happens but:
		// this happens when an entity is deleted before
		// it is added to the world, ie; created and deleted
		// before World#process has been called
		newlyCreatedEntityIds.set(entityId, false);

		recyclingEntityFactory.free(entityId);
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
		return (entities.size() > entityId) && !recyclingEntityFactory.has(entityId);
	}

	public boolean isNew(int entityId) {
		return newlyCreatedEntityIds.get(entityId);
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
	@Deprecated
	protected int getEntity(int entityId) {
		return entityId;
	}

	protected int getIdentity(int entityId) {
		int identity = entityToIdentity.get(entityId);
		if (identity == 0)
			identity = forceResolveIdentity(entityId);

		return identity;
	}

	void setIdentity(int e, TransmuteOperation operation) {
		entityToIdentity.set(e, operation.compositionId);
	}

	private int forceResolveIdentity(int entityId) {
		updateCompositionIdentity(EntityHelper.edit(world, entityId));
		return entityToIdentity.get(entityId);
	}

	void synchronize(EntitySubscription es) {
		for (int i = 1; highestSeenIdentity >= i; i++) {
			BitSet componentBits = identityResolver.composition.get(i);
			es.processComponentIdentity(i, componentBits);
		}

		// @todo int bitset iteration.
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i)) es.check(i);
		}

		es.informEntityChanges();
		es.rebuildCompressedActives();
	}
	
	public int createFlyweight() {
		//#include "./entity_set_flyweight_true.inc"
		return createEntity(-1);
	}
	
	/**
	 * Instantiates an EntityHelper without registering it into the world.
	 * @param id The ID to be set on the EntityHelper
	 */
	@Deprecated
	protected int createEntity(int id) {
		return id;
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
		
		int obtain() {
			if (limbo.isEmpty()) {
				int e = em.createEntity(nextId++);
				em.entities.set(e);
				return e;
			} else {
				int id = limbo.popFirst();
				recycled.set(id, false);
				return id;
			}
		}

		boolean has(int entityId) {
			return recycled.get(entityId);
		}
	}
}
