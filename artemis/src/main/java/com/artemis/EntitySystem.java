package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.Arrays;

import static com.artemis.utils.reflect.ReflectionUtil.implementsObserver;


/**
 * Tracks a subset of entities, but does not implement any sorting or iteration.
 *
 * Like {@link BaseEntitySystem}, but uses Entity references instead of int.
 *
 * This system exists as a convenience for users migrating from other Artemis
 * clones or older versions of odb. We recommend using the int systems over
 * the Entity variants.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class EntitySystem extends BaseEntitySystem
		implements EntitySubscription.SubscriptionListener {

	static final int FLAG_INSERTED = 1;
	static final int FLAG_REMOVED = 1 << 1;

	private boolean shouldSyncEntities;
	private WildBag<Entity> entities = new WildBag<Entity>();

	private int methodFlags;

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

	/**
	 * Set the world this system works on.
	 *
	 * @param world
	 *			the world to set
	 */
	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		if(implementsObserver(this, "inserted"))
			methodFlags |= FLAG_INSERTED;
		if(implementsObserver(this, "removed"))
			methodFlags |= FLAG_REMOVED;
	}

	@Override
	public final void inserted(IntBag entities) {
		shouldSyncEntities = true;
		// performance hack, skip calls to entities if system lacks implementation of added.
		if ((methodFlags & FLAG_INSERTED) > 0)
			super.inserted(entities);
	}

	@Override
	protected final void inserted(int entityId) {
		inserted(world.getEntity(entityId));
	}

	@Override
	public final void removed(IntBag entities) {
		shouldSyncEntities = true;
		// performance hack, skip calls to entities if system lacks implementation of deleted.
		if ((methodFlags & FLAG_REMOVED) > 0)
			super.removed(entities);
	}

	@Override
	protected final void removed(int entityId) {
		removed(world.getEntity(entityId));
	}

	public void inserted(Entity e) {
		throw new RuntimeException("everything changes");
	}

	public void removed(Entity e) {
		throw new RuntimeException("everything breaks");
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
