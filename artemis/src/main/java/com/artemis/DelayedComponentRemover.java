package com.artemis;

import com.artemis.utils.Bag;

import java.util.BitSet;

class DelayedComponentRemover<A extends Component> extends ComponentRemover<A> {
	final BitSet idBits = new BitSet();
	final BatchChangeProcessor batchProcessor;

	DelayedComponentRemover(Bag<A> components, ComponentPool pool, BatchChangeProcessor batchProcessor) {
		super(components, pool);
		this.batchProcessor = batchProcessor;
	}

	@Override
	void mark(int entityId) {
		if (idBits.isEmpty()) // see cm#clean
			batchProcessor.purgatories.add(this);

		idBits.set(entityId);
	}

	@Override
	boolean unmark(int entityId) {
		if (idBits.get(entityId)) {
			idBits.set(entityId, false);
			if (pool != null) {
				pool.free((PooledComponent) components.get(entityId));
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	void purge() {
		if (pool != null)
			purgeWithPool();
		else
			purgeNoPool();

		idBits.clear();
	}

	@Override
	boolean has(int entityId) {
		return idBits.get(entityId);
	}

	private void purgeWithPool() {
		for (int id = idBits.nextSetBit(0); id >= 0; id = idBits.nextSetBit(id + 1)) {
			A c = components.get(id);
				pool.free((PooledComponent) c);

			components.unsafeSet(id, null);
		}
	}

	private void purgeNoPool() {
		for (int id = idBits.nextSetBit(0); id >= 0; id = idBits.nextSetBit(id + 1)) {
			components.unsafeSet(id, null);
		}
	}
}
