package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;

/**
 * A typical entity system.
 * <p>
 * Use this when you need to process entities possessing the provided component
 * types.
 * </p>
 * 
 * @author Arni Arent
 */
public abstract class EntityProcessingSystem extends EntitySystem {
	/**
	 * Creates a new EntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match entities
	 */
	public EntityProcessingSystem(Aspect.Builder aspect) {
		super(aspect);
	}

	/**
	 * Process a entity this system is interested in.
	 *
	 * @param e
	 *			the entity to process
	 */
	protected abstract void process(Entity e);

	@Override
	protected final void processSystem() {
		int[] array = actives.getData();
		Entity e = flyweight;
		for (int i = 0, s = actives.size(); s > i; i++) {
			e.id = array[i];
			process(e);
		}
	}
}
