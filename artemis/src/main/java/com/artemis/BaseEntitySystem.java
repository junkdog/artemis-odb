package com.artemis;

import com.artemis.utils.IntBag;

/**
 * Base entity system for iterating a entities matching a single Aspect.
 *
 * @author Arni Arent
 */
public abstract class BaseEntitySystem extends BaseSystem
		implements EntitySubscription.SubscriptionListener {

	private final Aspect.Builder aspectConfiguration;
	protected EntitySubscription subscription;
	private WildBag<Entity> entities = new WildBag<Entity>();

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public BaseEntitySystem(Aspect.Builder aspect) {
		super();
		if (aspect == null) {
			String error = "Aspect.Builder was null; to use systems which " +
					"do not subscribe to entities, extend BaseSystem directly.";
			throw new NullPointerException(error);
		}

		aspectConfiguration = aspect;
	}

	protected void setWorld(World world) {
		super.setWorld(world);

		subscription = getSubscription();
		subscription.addSubscriptionListener(this);
	}

	public EntitySubscription getSubscription() {
		AspectSubscriptionManager sm = world.getSystem(AspectSubscriptionManager.class);
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
	 * Called if the system has received a entity it is interested in, e.g
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
	 * Called if a entity was removed from this system, e.g deleted or had one
	 * of it's components removed.
	 *
	 * @param entityId
	 *			the entity that was removed from this system
	 */
	protected void removed(int entityId) {}
}
