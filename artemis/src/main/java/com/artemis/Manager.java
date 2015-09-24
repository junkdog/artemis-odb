package com.artemis;

import com.artemis.utils.IntBag;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 * @author Daan van Yperen
 */
public abstract class Manager extends BaseSystem implements EntityObserver {

	public void added(int entityId) {}
	public void changed(int entityId) {}
	public void deleted(int entityId) {}

	@Override
	public final void added(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(ids[i]);
		}
	}

	@Override
	public final void changed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			changed(ids[i]);
		}
	}

	@Override
	public final void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(ids[i]);
		}
	}

	@Override
	protected final void processSystem() {
		// empty on purpose.
	}
}
