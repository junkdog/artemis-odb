package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;

/**
 * If you need to process entities at a certain interval then use this.
 * A typical usage would be to regenerate ammo or health at certain intervals, no need
 * to do that every game loop, but perhaps every 100 ms. or every second.
 * 
 * @author Arni Arent
 *
 */
public abstract class IntervalEntityProcessingSystem extends IntervalEntitySystem {

	public IntervalEntityProcessingSystem(Aspect aspect, float interval) {
		super(aspect, interval);
	}


	/**
	 * Process a entity this system is interested in.
	 * @param e the entity to process.
	 */
	protected abstract void process(Entity e);

	
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		for (int i = 0, s = entities.size(); s > i; i++) {
			process(entities.get(i));
		}
	}

}
