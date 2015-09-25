package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.Arrays;


/**
 * Entity system for iterating a entities matching a single Aspect. Likely the
 * most common type of system.
 *
 * @author Arni Arent
 */
public abstract class EntitySystem extends BaseEntitySystem
		implements EntitySubscription.SubscriptionListener {

	private boolean shouldSyncEntities;
	private WildBag<Entity> entities = new WildBag<Entity>();

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public EntitySystem(Aspect.Builder aspect) {
		super(aspect);
	}

	@Override
	public void inserted(IntBag entities) {
		super.inserted(entities);
		shouldSyncEntities = true;
	}

	@Override
	public void removed(IntBag entities) {
		super.removed(entities);
		shouldSyncEntities = true;
	}

	/**
	 * Gets the entities processed by this system. Do not delete entities from
	 * this bag - it is the live thing.
	 *
	 * @return System's entity bag, as matched by aspect.
	 */
	public Bag<Entity> getEntities() {
		if (shouldSyncEntities) {
			int oldSize = entities.size();
			entities.setSize(0);
			IntBag entityIds = subscription.getEntities();
			int[] ids = entityIds.getData();
			for (int i = 0; i < entityIds.size(); i++) {
				entities.add(world.getEntity(ids[i]));
			}

			if (oldSize > entities.size()) {
				Arrays.fill(entities.getData(), entities.size(), oldSize, null);
			}

			shouldSyncEntities = false;
		}

		return entities;
	}
}
