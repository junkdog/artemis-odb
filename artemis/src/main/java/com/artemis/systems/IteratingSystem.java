package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.utils.IntBag;

/**
 * A typical entity system.
 * <p>
 * Use this when you need to process entities possessing the provided component
 * types.
 * </p>
 * 
 * @author Arni Arent
 */
public abstract class IteratingSystem extends EntitySystem {

	/**
	 * Creates a new EntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match entities
	 */
	public IteratingSystem(Aspect.Builder aspect) {
		super(aspect);
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
	}

	/**
	 * Process a entity this system is interested in.
	 *
	 * @param entityId
	 *			the entity to process
	 */
	protected abstract void process(int entityId);

	@Override
	protected final void processSystem() {
		IntBag actives = subscription.getEntities();
		int[] array = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			process(array[i]);
		}
	}
}
