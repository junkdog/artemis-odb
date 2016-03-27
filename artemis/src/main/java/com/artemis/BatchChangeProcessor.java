package com.artemis;

import com.artemis.utils.Bag;

import java.util.BitSet;

final class BatchChangeProcessor {

	final BitSet changed = new BitSet();
	final BitSet deleted = new BitSet();

	private World world;

	private final Bag<EntityEdit> pool = new Bag<EntityEdit>();
	private WildBag<EntityEdit> edited = new WildBag<EntityEdit>();

	BatchChangeProcessor(World world) {
		this.world = world;
	}

	/**
	 * Get entity editor.
	 * @return a fast albeit verbose editor to perform batch changes to entities.
	 * @param entityId entity to fetch editor for.
	 */
	EntityEdit obtainEditor(int entityId) {
		if (!edited.isEmpty() && edited.get(edited.size() - 1).getEntityId() == entityId)
			return edited.get(edited.size() - 1);

		EntityEdit edit = entityEdit();
		edited.add(edit);

		edit.entityId = entityId;

		if (!world.getEntityManager().isActive(entityId))
			throw new RuntimeException("Issued edit on deleted " + edit.entityId);

		return edit;
	}

	private EntityEdit entityEdit() {
		if (pool.isEmpty()) {
			return new EntityEdit(world);
		} else {
			return pool.removeLast();
		}
	}

	void update(AspectSubscriptionManager asm) {
		while(!changed.isEmpty() || !deleted.isEmpty()) {
			asm.process(changed, deleted);
		}

		clean();
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
