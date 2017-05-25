package com.artemis;

import com.artemis.utils.Bag;

public class ImmediateComponentRemover<A extends Component> extends ComponentRemover<A> {
	public ImmediateComponentRemover(Bag<A> components, ComponentPool pool) {
		super(components, pool);
	}

	@Override
	void mark(int entityId) {
		if (pool != null) {
			PooledComponent c = (PooledComponent) components.get(entityId);
			if (c != null) pool.free(c);
		}
		components.unsafeSet(entityId, null);
	}

	@Override
	boolean unmark(int entityId) {
		return false;
	}

	@Override
	void purge() {}

	@Override
	boolean has(int entityId) {
		return false;
	}
}
