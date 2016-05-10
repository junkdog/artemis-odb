package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.ShortBag;

import java.util.BitSet;

import static com.artemis.utils.ConverterUtil.toIntBag;

/**
 * Maintains the list of entities matched by an aspect. Entity subscriptions
 * are automatically updated during {@link com.artemis.World#process()}.
 * Any {@link com.artemis.EntitySubscription.SubscriptionListener | listeners}
 * are informed when entities are added or removed.
 */
public class EntitySubscription {
	final SubscriptionExtra extra;

	private final IntBag entities;
	private final BitSet activeEntityIds;
	private final ShortBag entityToIdentity;

	private final BitSet insertedIds;
	private final BitSet removedIds;

	final BitSet aspectCache = new BitSet();

	EntitySubscription(World world, Aspect.Builder builder) {
		extra = new SubscriptionExtra(builder.build(world), builder);
		entityToIdentity = world.getComponentManager().entityToIdentity;

		activeEntityIds = new BitSet();
		entities = new IntBag();

		insertedIds = new BitSet();
		removedIds = new BitSet();
	}

	/**
	 * Returns a reference to the bag holding all matched
	 * entities.
	 *
	 * <p><b>Warning: </b> Never remove elements from the bag, as this
	 * will lead to undefined behavior.</p>
	 *
	 * @return View of all active entities.
	 */
	public IntBag getEntities() {
		if (entities.isEmpty() && !activeEntityIds.isEmpty())
			rebuildCompressedActives();

		return entities;
	}

	/**
	 * Returns the bitset tracking all matched entities.
	 *
	 * <p><b>Warning: </b> Never toggle bits in the bitset, as
	 * this <i>may</i> lead to erroneously added or removed entities.</p>
	 *
	 * @return View of all active entities.
	 */
	public BitSet getActiveEntityIds() {
		return activeEntityIds;
	}

	/**
	 * @return aspect used for matching entities to subscription.
	 */
	public Aspect getAspect() {
		return extra.aspect;
	}

	/**
	 * @return aspect builder used for matching entities to subscription.
	 */
	public Aspect.Builder getAspectBuilder() {
		return extra.aspectReflection;
	}

	/**
	 * A new unique component composition detected, check if this
	 * subscription's aspect is interested in it.
	 */
	void processComponentIdentity(int id, BitSet componentBits) {
		aspectCache.set(id, extra.aspect.isInterested(componentBits));
	}

	void rebuildCompressedActives() {
		BitSet bs = activeEntityIds;
		int size = bs.cardinality();
		entities.setSize(size);
		entities.ensureCapacity(size);
		int[] activesArray = entities.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}
	}

	final void check(int id) {
		boolean interested = aspectCache.get(entityToIdentity.get(id));
		boolean contains = activeEntityIds.get(id);

		if (interested && !contains) {
			insert(id);
		} else if (!interested && contains) {
			remove(id);
		}
	}

	private void remove(int entityId) {
		activeEntityIds.clear(entityId);
		removedIds.set(entityId);
	}

	private void insert(int entityId) {
		activeEntityIds.set(entityId);
		insertedIds.set(entityId);
	}

	void process(IntBag changed, IntBag deleted) {
		deleted(deleted);
		changed(changed);

		informEntityChanges();
	}

	void processAll(IntBag changed, IntBag deleted) {
		deletedAll(deleted);
		changed(changed);

		informEntityChanges();
	}

	void informEntityChanges() {
		if (insertedIds.isEmpty() && removedIds.isEmpty())
			return;

		transferBitsToInts(extra.inserted, extra.removed);
		extra.informEntityChanges();
		entities.setSize(0);
	}

	private void transferBitsToInts(IntBag inserted, IntBag removed) {
		toIntBag(insertedIds, inserted);
		toIntBag(removedIds, removed);
		insertedIds.clear();
		removedIds.clear();
	}

	private void changed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check(ids[i]);
		}
	}

	private void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(ids[i]);
		}
	}

	private void deletedAll(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			int id = ids[i];
			activeEntityIds.clear(id);
			removedIds.set(id);
		}
	}

	private void deleted(int entityId) {
		if(activeEntityIds.get(entityId))
			remove(entityId);
	}

	/**
	 * Add listener interested in changes to the subscription.
	 *
	 * @param listener listener to add.
	 */
	public void addSubscriptionListener(SubscriptionListener listener) {
		extra.listeners.add(listener);
	}

	@Override
	public String toString() {
		return "EntitySubscription[" + getAspectBuilder() + "]";
	}

	/**
	 * <p>This interfaces reports entities inserted or
	 * removed when matched against their {@link com.artemis.EntitySubscription}</p>
	 */
	public interface SubscriptionListener {
		/**
		 * Called after entities have been matched and inserted into an
		 * EntitySubscription.
		 */
		void inserted(IntBag entities);

		/**
		 * <p>Called after entities have been removed from an EntitySubscription.
		 * Explicitly removed components are only retrievable at this point
		 * if annotated with {@link DelayedComponentRemoval}.</p>
		 *
		 * <p>Deleted entities retain all their components until - all listeners
		 * have been informed.</p>
		 */
		void removed(IntBag entities);
	}

	public static class SubscriptionExtra {
		final IntBag inserted = new IntBag();
		final IntBag removed = new IntBag();
		final Aspect aspect;
		final Aspect.Builder aspectReflection;
		final Bag<SubscriptionListener> listeners = new Bag<SubscriptionListener>();

		public SubscriptionExtra(Aspect aspect, Aspect.Builder aspectReflection) {
			this.aspect = aspect;
			this.aspectReflection = aspectReflection;
		}

		void informEntityChanges() {
			informListeners();

			removed.setSize(0);
			inserted.setSize(0);
		}

		private void informListeners() {
			for (int i = 0, s = listeners.size(); s > i; i++) {
				if (removed.size() > 0)
					listeners.get(i).removed(removed);

				if (inserted.size() > 0)
					listeners.get(i).inserted(inserted);
			}
		}
	}
}
