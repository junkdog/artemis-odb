package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;

/**
 * Entity reference iterating system.
 * <p/>
 * System that iterates over {@link EntitySubscription} member entities by
 * entity reference.
 * <p/>
 * Use this when you need to process entities matching an {@link Aspect},
 * and you prefer to work with {@link Entity}.
 * <p/>
 * This is a convenience system. We suggest to use {@link IteratingSystem}
 * instead, it sits closer to the metal and enjoys better long term support.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class EntityProcessingSystem extends EntitySystem {
	/**
	 * Creates a new EntityProcessingSystem.
	 * @param aspect
	 * 		the aspect to match entities
	 */
	public EntityProcessingSystem(Aspect.Builder aspect) {
		super(aspect);
	}

	/**
	 * Process a entity this system is interested in.
	 * @param e
	 * 		the entity to process
	 */
	protected abstract void process(Entity e);

	/** @inheritDoc */
	@Override
	protected final void processSystem() {
		Bag<Entity> entities = getEntities();
		Object[] array = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			process((Entity) array[i]);
		}
	}
}
