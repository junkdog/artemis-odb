package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;

import java.util.BitSet;

public class EntitySubscription {
	final Aspect aspect;
	private final Aspect.Builder aspectReflection;
	private final BitSet aspectCache;

	private final IntBag entities;
	private final BitSet activeEntityIds;
	private final EntityManager em;

	private final Bag<SubscriptionListener> listeners;

	private final WildBag<Entity> inserted;
	private final WildBag<Entity> removed;

	EntitySubscription(World world, Aspect.Builder builder) {
		aspect = builder.build(world);
		aspectReflection = builder;
		aspectCache = new BitSet();
		em = world.getEntityManager();

		activeEntityIds = new BitSet();
		entities = new IntBag();

		listeners = new Bag<SubscriptionListener>();

		inserted = new WildBag<Entity>();
		removed = new WildBag<Entity>();
	}

	public IntBag getEntities() {
		return entities;
	}

	public BitSet getActiveEntityIds() {
		return activeEntityIds;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public Aspect.Builder getAspectBuilder() {
		return aspectReflection;
	}

	/**
	 * A new unique component composition detected, check if this
	 * subscription's aspect is interested in it.
	 */
	void processComponentIdentity(int id, BitSet componentBits) {
		aspectCache.set(id, aspect.isInterested(componentBits));
	}

	private void rebuildCompressedActives() {
		BitSet bs = activeEntityIds;
		int size = bs.cardinality();
		entities.setSize(size);
		entities.ensureCapacity(size);
		int[] activesArray = entities.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}
	}

	private final void check(Entity e) {
		int id = e.getId();
		boolean interested = aspectCache.get(em.getIdentity(e)) && em.isActive(id) && em.isEnabled(id);
		boolean contains = activeEntityIds.get(id);

		if (interested && !contains) {
			insert(e);
		} else if (!interested && contains) {
			remove(e);
		}
	}

	private void remove(Entity e) {
		activeEntityIds.clear(e.getId());
		removed.add(e);
	}

	private void insert(Entity e) {
		activeEntityIds.set(e.getId());
		inserted.add(e);
	}

	void process(WildBag<Entity> added, WildBag<Entity> changed, WildBag<Entity> deleted) {
		added(added);
		changed(changed);
		deleted(deleted);

		if (informEntityChanges())
			rebuildCompressedActives();
	}

	private boolean informEntityChanges() {
		if (inserted.isEmpty() && removed.isEmpty())
			return false;

		for (int i = 0, s = listeners.size(); s > i; i++) {
			if (inserted.size() > 0)
				listeners.get(i).inserted(inserted);

			if (removed.size() > 0)
				listeners.get(i).removed(removed);
		}

		inserted.setSize(0);
		removed.setSize(0);

		return true;
	}

	private final void added(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check((Entity)data[i]);
		}
	}

	private final void changed(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check((Entity)data[i]);
		}
	}

	private final void deleted(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted((Entity) data[i]);
		}
	}

	private final void deleted(Entity e) {
		if(activeEntityIds.get(e.getId()))
			remove(e);
	}

	public void addSubscriptionListener(SubscriptionListener listener) {
		listeners.add(listener);
	}

	public static interface SubscriptionListener {
		/**
		 * Called after entities have been matched and inserted into an
		 * EntitySubscription.
		 */
		void inserted(ImmutableBag<Entity> entities);

		/**
		 * Called after entities have been removed from an EntitySubscription.
		 */
		void removed(ImmutableBag<Entity> entities);
	}

}
