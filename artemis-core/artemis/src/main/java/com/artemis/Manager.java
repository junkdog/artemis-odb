package com.artemis;

import com.artemis.utils.IntBag;

import static com.artemis.Aspect.all;
import static com.artemis.EntitySystem.FLAG_INSERTED;
import static com.artemis.EntitySystem.FLAG_REMOVED;
import static com.artemis.utils.reflect.ReflectionUtil.implementsObserver;


/**
 * A manager for handling entities in the world.
 *
 * In odb Manager has been absorbed into the {@link BaseSystem} hierarchy.
 * While Manager is still available we recommend implementing new
 * managers using IteratingSystem, {@link BaseEntitySystem} with
 * {@link Aspect#all()}, or {@link BaseSystem} depending on your needs.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class Manager extends BaseSystem {
	private int methodFlags;

	/** Called when entity gets added to world. */
	public void added(Entity e) {
		throw new RuntimeException("I shouldn't be here...");
	}

	/** Called when entity gets deleted from world. */
	public void deleted(Entity e) {
		throw new RuntimeException("... if it weren't for the tests.");
	}

	/**
	 * Set the world this system works on.
	 *
	 * @param world
	 *			the world to set
	 */
	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		if(implementsObserver(this, "added"))
			methodFlags |= FLAG_INSERTED;
		if(implementsObserver(this, "deleted"))
			methodFlags |= FLAG_REMOVED;
	}

	/** Hack to register manager to right subscription */
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
		// performance hack, skip if manager lacks implementation of inserted.
		if ((methodFlags & FLAG_INSERTED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(world.getEntity(ids[i]));
		}
	}

	private void deleted(IntBag entities) {
		// performance hack, skip if manager lacks implementation of removed.
		if ((methodFlags & FLAG_REMOVED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(world.getEntity(ids[i]));
		}
	}

	/** Managers are not interested in processing. */
	@Override
	protected final void processSystem() {}
}
