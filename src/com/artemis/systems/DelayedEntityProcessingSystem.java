package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;

public abstract class DelayedEntityProcessingSystem extends DelayedEntitySystem {
	
	protected DelayedEntityProcessingSystem(Aspect aspect) {
		super(aspect);
	}

	/**
	 * Process a entity this system is interested in.
	 * @param e the entity to process.
	 */
	protected abstract void process(Entity e, int accumulatedDelta);

	@Override
	protected final void processEntities(ImmutableBag<Entity> entities, int accumulatedDelta) {
		for (int i = 0, s = entities.size(); s > i; i++) {
			process(entities.get(i), accumulatedDelta);
		}
	}

}
