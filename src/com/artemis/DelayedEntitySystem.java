package com.artemis;

import com.artemis.utils.ImmutableBag;

/**
 * The purpose of this class is to allow systems to execute at varying intervals.
 * 
 * An example system would be an ExpirationSystem, that deletes entities after a certain
 * lifetime. Instead of running a system that decrements a timeLeft value for each
 * entity, you can simply use this system to execute in a future at a time of the shortest
 * lived entity, and then reset the system to run at a time in a future at a time of the
 * shortest lived entity, etc.
 * 
 * Another example system would be an AnimationSystem. You know when you have to animate
 * a certain entity, e.g. in 300 milliseconds. So you can set the system to run in 300 ms.
 * to perform the animation.
 * 
 * This will save CPU cycles in some scenarios.
 * 
 * Make sure you detect all circumstances that change. E.g. if you create a new entity you
 * should find out if you need to run the system sooner than scheduled, or when deleting
 * a entity, maybe something changed and you need to recalculate when to run. Usually this
 * applies to when entities are created, deleted, changed.
 * 
 * This class offers public methods allowing external systems to use it.
 * 
 * @author Arni Arent
 *
 */
public abstract class DelayedEntitySystem extends EntitySystem {
	private int delay;
	private boolean running;
	private int acc;

	public DelayedEntitySystem(Class<? extends Component>... types) {
		super(types);
	}

	@Override
	protected final void processEntities(ImmutableBag<Entity> entities) {
		processEntities(entities, acc);
		stop();
	}
	
	@Override
	protected final boolean checkProcessing() {
		if(running) {
			acc += world.getDelta();
			
			if(acc >= delay) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The entities to process with accumulated delta.
	 * @param entities read-only bag of entities.
	 */
	protected abstract void processEntities(ImmutableBag<Entity> entities, int accumulatedDelta);
	
	
	
	
	/**
	 * Start processing of entities after a certain amount of milliseconds.
	 * 
	 * Cancels current delayed run and starts a new one.
	 * 
	 * @param delay time delay in milliseconds until processing starts.
	 */
	public void startDelayedRun(int delay) {
		this.delay = delay;
		this.acc = 0;
		running = true;
	}

	/**
	 * Get the initial delay that the system was ordered to process entities after.
	 * 
	 * @return the originally set delay.
	 */
	public int getInitialTimeDelay() {
		return delay;
	}
	
	public int getRemainingTimeUntilProcessing() {
		if(running) {
			return delay-acc;
		}
		return 0;
	}
	
	/**
	 * Check if the system is counting down towards processing.
	 * 
	 * @return true if it's counting down, false if it's not running.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Aborts running the system in the future and stops it. Call delayedRun() to start it again.
	 */
	public void stop() {
		this.running = false;
		this.acc = 0;
	}

}
