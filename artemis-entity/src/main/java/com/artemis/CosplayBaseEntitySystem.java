package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.utils.IntBag;

/**
 * Tracks a subset of entities, but does not implement any sorting or iteration.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class CosplayBaseEntitySystem<T extends Entity> extends CosplayBaseSystem<T>
		implements EntitySubscription.SubscriptionListener {

	private final Aspect.Builder aspectConfiguration;
	protected EntitySubscription subscription;

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public CosplayBaseEntitySystem(Aspect.Builder aspect) {
		super();
		if (aspect == null) {
			String error = "Aspect.Builder was null; to use systems which " +
					"do not subscribe to entities, extend BaseSystem directly.";
			throw new NullPointerException(error);
		}

		aspectConfiguration = aspect;
	}

	protected void setWorld(AbstractEntityWorld<T> world) {
		super.setWorld(world);
		subscription = getSubscription();
	   	subscription.addSubscriptionListener(this);
	}

	/**
	 * @return entity subscription backing this system.
	 */
	public EntitySubscription getSubscription() {
		final AspectSubscriptionManager sm = world.getAspectSubscriptionManager();
		return sm.get(aspectConfiguration);
	}

	@Override
	public void inserted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			inserted(ids[i]);
		}
	}

	/**
	 * Gets the entities processed by this system. Do not delete entities from
	 * this bag - it is the live thing.
	 *
	 * @return System's entity ids, as matched by aspect.
	 */
	public IntBag getEntityIds() {
		return subscription.getEntities();
	}

	/**
	 * Called if entity has come into scope for this system, e.g
	 * created or a component was added to it.
	 *
	 * @param entityId
	 *			the entity that was added to this system
	 */
	protected void inserted(int entityId) {}

	@Override
	public void removed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			removed(ids[i]);
		}
	}

	/**
	 * <p>Called if entity has gone out of scope of this system, e.g deleted
	 * or had one of it's components removed.</p>
	 *
	 * <p>Explicitly removed components are only retrievable at this point
	 * if annotated with {@link DelayedComponentRemoval}.</p>
	 *
	 * <p>Deleted entities retain all their components - until all listeners
	 * have been informed.</p>
	 *
	 * @param entityId
	 *			the entity that was removed from this system
	 */
	protected void removed(int entityId) {}
}
