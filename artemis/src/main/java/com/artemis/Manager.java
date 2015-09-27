package com.artemis;

import com.artemis.utils.IntBag;

import static com.artemis.Aspect.all;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 * @author Daan van Yperen
 */
public abstract class Manager extends BaseSystem {

	public void added(Entity e) {}
	public void deleted(Entity e) {}

	protected void registerManager() {
		world.getAspectSubscriptionManager()
				.get(all())
				.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
					@Override
					public void inserted(IntBag entities) {
						added(entities);
					}

					@Override
					public void removed(IntBag entities) {
						deleted(entities);
					}
				});
	}

	private void added(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(world.getEntity(ids[i]));
		}
	}

	private void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(world.getEntity(ids[i]));
		}
	}

	@Override
	protected final void processSystem() {}
}
