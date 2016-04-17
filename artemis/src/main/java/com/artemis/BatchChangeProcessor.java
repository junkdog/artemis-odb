package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.ConverterUtil;
import com.artemis.utils.IntBag;

import java.util.BitSet;

final class BatchChangeProcessor {
	private final World world;
	private final AspectSubscriptionManager asm;

	final BitSet changed = new BitSet();
	private final BitSet deleted = new BitSet();
	private final BitSet pendingPurge = new BitSet();
	private final IntBag toPurge = new IntBag();

	private final Bag<EntityEdit> pool = new Bag<EntityEdit>();
	private final WildBag<EntityEdit> edited = new WildBag(EntityEdit.class);

	BatchChangeProcessor(World world) {
		this.world = world;
		asm = world.getAspectSubscriptionManager();
	}

	boolean isDeleted(int entityId) {
		return pendingPurge.get(entityId);
	}

	void delete(int entityId) {
		deleted.set(entityId);
		pendingPurge.set(entityId);

		// guarding against previous transmutations
		changed.set(entityId, false);
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
		}

		clean();
	}

	IntBag getPendingPurge() {
		ConverterUtil.toIntBag(pendingPurge, toPurge);
		pendingPurge.clear();
		return toPurge;
	}

	private boolean clean() {
		if (edited.isEmpty())
			return false;

		edited.setSize(0);
		Object[] data = edited.getData();
		for (int i = 0, s = edited.size(); s > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			pool.add(edit);
		}

		return true;
	}
}
