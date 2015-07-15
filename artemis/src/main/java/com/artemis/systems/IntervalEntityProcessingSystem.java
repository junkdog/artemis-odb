package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;


/**
 * If you need to process entities at a certain interval then use this.
 * <p>
 * A typical usage would be to regenerate ammo or health at certain intervals,
 * no need to do that every game loop, but perhaps every 100 ms. or every
 * second.
 * </p>
 * 
 * @author Arni Arent
 */
public abstract class IntervalEntityProcessingSystem extends IntervalEntitySystem {
	private Entity flyweight;

	/**
	 * Creates a new IntervalEntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match entities
	 * @param interval
	 *			the interval at which the system is processed
	 */
	public IntervalEntityProcessingSystem(Aspect.Builder aspect, float interval) {
		super(aspect, interval);
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		flyweight = createFlyweightEntity();
	}


	/**
	 * Process a entity this system is interested in.
	 *
	 * @param e
	 *			the entity to process
	 */
	protected abstract void process(Entity e);

	@Override
	protected void processSystem() {
		processEntities(subscription.getEntities());
	}

	protected void processEntities(IntBag entities) {
		int[] ids = entities.getData();
		Entity e = flyweight;
		for (int i = 0, s = entities.size(); s > i; i++) {
			e.id = ids[i];
			process(e);
		}
	}

}
