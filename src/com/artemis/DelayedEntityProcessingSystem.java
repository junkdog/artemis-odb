package com.artemis;

import com.artemis.utils.ImmutableBag;

public abstract class DelayedEntityProcessingSystem extends DelayedEntitySystem {
	
	/**
	 * Create a new DelayedEntityProcessingSystem. It requires at least one component.
	 * @param requiredType the required component type.
	 * @param otherTypes other component types.
	 */
	public DelayedEntityProcessingSystem(Class<? extends Component> requiredType, Class<? extends Component>... otherTypes) {
		super(getMergedTypes(requiredType, otherTypes));
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
