package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import com.artemis.utils.BitVector;

final class BatchChangeProcessor {
	private final World world;
	private final AspectSubscriptionManager asm;

	final BitVector changed = new BitVector();
	final WildBag<ComponentRemover> purgatories = new WildBag<ComponentRemover>(ComponentRemover.class);

	// marked for deletion, will be removed for entity subscriptions asap
	private final BitVector deleted = new BitVector();

	// collected deleted entities during this {@link World#process()} round;
	// cleaned at end of round.
	private final BitVector pendingPurge = new BitVector();
	private final IntBag toPurge = new IntBag();

	private final Bag<EntityEdit> pool = new Bag<EntityEdit>();
	private final WildBag<EntityEdit> edited = new WildBag(EntityEdit.class);

	BatchChangeProcessor(World world) {
		this.world = world;
		asm = world.getAspectSubscriptionManager();

		EntityManager em = world.getEntityManager();
		em.registerEntityStore(changed);
		em.registerEntityStore(deleted);
		em.registerEntityStore(pendingPurge);
	}

	boolean isDeleted(int entityId) {
		return pendingPurge.unsafeGet(entityId);
	}

	void delete(int entityId) {
		deleted.unsafeSet(entityId);
		pendingPurge.unsafeSet(entityId);

		// guarding against previous transmutations
		changed.unsafeClear(entityId);
	}

	/**
	 * Get entity editor.
	 * @return a fast albeit verbose editor to perform batch changes to entities.
	 * @param entityId entity to fetch editor for.
	 */
	EntityEdit obtainEditor(int entityId) {
		int size = edited.size();
		if (size != 0 && edited.get(size - 1).getEntityId() == entityId)
			return edited.get(size - 1);

		EntityEdit edit = entityEdit();
		edited.add(edit);

		edit.entityId = entityId;

		return edit;
	}

	private EntityEdit entityEdit() {
		if (pool.isEmpty()) {
			return new EntityEdit(world);
		} else {
			return pool.removeLast();
		}
	}

	void update() {
		while(!changed.isEmpty() || !deleted.isEmpty()) {
			asm.process(changed, deleted);
			purgeComponents();
		}

		clean();
	}

	void purgeComponents() {
		for (int i = 0, s = purgatories.size(); s > i; i++)
			purgatories.get(i).purge();

		purgatories.setSize(0);
	}

	IntBag getPendingPurge() {
		pendingPurge.toIntBag(toPurge);
		pendingPurge.clear();
		return toPurge;
	}

	private boolean clean() {
		if (edited.isEmpty())
			return false;

		Object[] data = edited.getData();
		for (int i = 0, s = edited.size(); s > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			pool.add(edit);
		}
		edited.setSize(0);

		return true;
	}
}
