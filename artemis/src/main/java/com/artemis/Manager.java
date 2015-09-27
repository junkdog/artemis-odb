package com.artemis;

import com.artemis.utils.IntBag;

import static com.artemis.Aspect.all;
import static com.artemis.EntitySystem.FLAG_INSERTED;
import static com.artemis.EntitySystem.FLAG_REMOVED;
import static com.artemis.utils.reflect.ReflectionUtil.implementsObserver;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 * @author Daan van Yperen
 */
public abstract class Manager extends BaseSystem {
	private int methodFlags;

	public void added(Entity e) {
		throw new RuntimeException("I shouldn't be here...");
	}

	public void deleted(Entity e) {
		throw new RuntimeException("... if it weren't for the tests.");
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		if(implementsObserver(this, "added"))
			methodFlags |= FLAG_INSERTED;
		if(implementsObserver(this, "deleted"))
			methodFlags |= FLAG_REMOVED;
	}

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
		if ((methodFlags & FLAG_INSERTED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(world.getEntity(ids[i]));
		}
	}

	private void deleted(IntBag entities) {
		if ((methodFlags & FLAG_REMOVED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(world.getEntity(ids[i]));
		}
	}

	@Override
	protected final void processSystem() {}
}
