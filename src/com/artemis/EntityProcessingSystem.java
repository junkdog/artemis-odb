package com.artemis;

import com.artemis.utils.ImmutableBag;

/**
 * A typical entity system. Use this when you need to process entities possessing the
 * provided component types.
 * 
 * @author Arni Arent
 *
 */
public abstract class EntityProcessingSystem extends EntitySystem {
	
	/**
	 * Create a new EntityProcessingSystem. It requires at least one component.
	 * @param requiredType the required component type.
	 * @param otherTypes other component types.
	 */
	public EntityProcessingSystem(Class<? extends Component> requiredType, Class<? extends Component>... otherTypes) {
		super(getMergedTypes(requiredType, otherTypes));
	}
	
	/**
	 * Process a entity this system is interested in.
	 * @param e the entity to process.
	 */
	protected abstract void process(Entity e);

	@Override
	protected final void processEntities(ImmutableBag<Entity> entities) {
		for (int i = 0, s = entities.size(); s > i; i++) {
			process(entities.get(i));
		}
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
}
