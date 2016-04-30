package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.utils.IntBag;


/**
 * Process a subset of entities every x ticks.
 * <p>
 * A typical usage would be to regenerate ammo or health at certain intervals,
 * no need to do that every game loop, but perhaps every 100 ms. or every
 * second.
 * </p>
 * 
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class IntervalIteratingSystem extends IntervalSystem {
	/**
	 * Creates a new IntervalEntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match entities
	 * @param interval
	 *			the interval at which the system is processed
	 */
	public IntervalIteratingSystem(Aspect.Builder aspect, float interval) {
		super(aspect, interval);
	}


	/**
	 * Process a entity this system is interested in.
	 *
	 * @param entityId
	 *			the entity to process
	 */
	protected abstract void process(int entityId);

	@Override
	protected void processSystem() {
		IntBag entities = subscription.getEntities();
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			process(ids[i]);
		}
	}
}
