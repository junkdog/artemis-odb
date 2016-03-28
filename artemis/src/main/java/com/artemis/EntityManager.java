package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.IntDeque;

import java.util.BitSet;

import static com.artemis.Aspect.all;


/**
 * Manages entity instances.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
@SkipWire
public class EntityManager extends BaseSystem {

	/** Adrian's secret rebellion. */
	static final int NO_COMPONENTS = 0;
	/** Contains all entities in the manager. */
	private final Bag<Entity> entities;
	/** Stores the bits of all currently disabled entities IDs. */
	private RecyclingEntityFactory recyclingEntityFactory;

	private IntBag pendingDeletion = new IntBag();

	ComponentIdentityResolver identityResolver = new ComponentIdentityResolver();
	private IntBag entityToIdentity = new IntBag();
	private int highestSeenIdentity;

	/**
	 * Creates a new EntityManager Instance.
	 */
	protected EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
	}

	@Override
	protected void processSystem() {}

	@Override
	protected void initialize() {
		recyclingEntityFactory = new RecyclingEntityFactory(this);
		world.getAspectSubscriptionManager()
				.get(all())
				.addSubscriptionListener(
						new EntitySubscription.SubscriptionListener() {
							@Override
							public void inserted(IntBag entities) {}

							@Override
							public void removed(IntBag entities) {
								pendingDeletion.addAll(entities);
							}
						});
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance() {
		Entity e = recyclingEntityFactory.obtain();
		entityToIdentity.set(e.getId(), 0);

		return e;
	}

	/**
	 * Create a new entity.
	 *
	 * @return a new entity id
	 */
	protected int create() {
		int id = recyclingEntityFactory.obtain().id;
		entityToIdentity.set(id, 0);
		return id;
	}

	/**
	 * Create a new entity based on the supplied archetype.
	 *
	 * @return a new entity id
	 */
	protected int create(Archetype archetype) {
		int id = recyclingEntityFactory.obtain().id;
		entityToIdentity.set(id, archetype.compositionId);
		return id;
	}

	/**
	 * Create a new entity based on the supplied archetype.
	 *
	 * @return a new entity
	 */
	protected Entity createEntityInstance(Archetype archetype) {
		Entity e = createEntityInstance();
		entityToIdentity.set(e.id, archetype.compositionId);
		return e;
	}

	/** Get component composition of entity. */
	BitSet componentBits(int entityId) {
		int identityIndex = entityToIdentity.get(entityId);
		return identityResolver.composition.get(identityIndex);
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
			world.getAspectSubscriptionManager()
					.processComponentIdentity(identity, componentBits);
			highestSeenIdentity = identity;
		}
		return identity;
	}

	void clean() {
		if (pendingDeletion.isEmpty())
			return;

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

		pendingDeletion.setSize(0);
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
	 * Fetch composition id for entity.
	 *
	 * A composition id is uniquely identified by a single Aspect. For performance reasons, each entity is
	 * identified by its composition id. Adding or removing components from an entity will change its compositionId.
	 *
	 * @param entityId
	 * @return composition identity.
	 */
	protected int getIdentity(int entityId) {
		return entityToIdentity.get(entityId);
	}

	/**
	 * Set composition id of entity.
	 *
	 * @param entityId entity id
	 * @param compositionId composition id
	 */
	void setIdentity(int entityId, int compositionId) {
		entityToIdentity.set(entityId, compositionId);
	}

	/**
	 * Synchronizes new subscriptions with {@link World} state.
	 *
	 * @param es entity subscription to update.
	 */
	void synchronize(EntitySubscription es) {
		for (int i = 1; highestSeenIdentity >= i; i++) {
			BitSet componentBits = identityResolver.composition.get(i);
			es.processComponentIdentity(i, componentBits);
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e != null && isActive(i))
				es.check(e.getId());
		}

		es.informEntityChanges();
		es.rebuildCompressedActives();
	}

	/**
	 * Instantiates an Entity without registering it into the world.
	 * @param id The ID to be set on the Entity
	 */
	protected Entity createEntity(int id) {
		return new Entity(world, id);
	}
	
	/** Tracks all unique component compositions. */
	private static final class ComponentIdentityResolver {
		private final Bag<BitSet> composition;
		
		ComponentIdentityResolver() {
			composition = new Bag<BitSet>();
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
				Entity e = em.createEntity(nextId++);
				em.entities.set(e.id, e);
				return e;
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
