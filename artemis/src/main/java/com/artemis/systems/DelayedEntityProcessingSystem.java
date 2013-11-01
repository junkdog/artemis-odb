package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;


/**
 * The purpose of this class is to allow systems to execute at varying
 * intervals.
 * <p>
 * An example system would be an ExpirationSystem, that deletes entities after
 * a certain lifetime. Instead of running a system that decrements a timeLeft
 * value for each entity, you can simply use this system to execute in a future
 * at a time of the shortest lived entity, and then reset the system to run at
 * a time in a future at a time of the shortest lived entity, etc.
 * </p><p>
 * Another example system would be an AnimationSystem. You know when you have
 * to animate a certain entity, e.g. in 300 milliseconds. So you can set the
 * system to run in 300 ms to perform the animation.
 * </p><p>
 * This will save CPU cycles in some scenarios.
 * </p><p>
 * Implementation notes:<br />
 * In order to start the system you need to override the
 * {@link #inserted(Entity) inserted(Entity e)} method, look up the delay time
 * from that entity and offer it to the system by using the
 * {@link #offerDelay(float) offerDelay(float delay)} method. Also, when
 * processing the entities you must also call
 * {@link #offerDelay(float) offerDelay(float delay)} for all valid entities.
 * </p><p>
 *
 * @author Arni Arent
 */
public abstract class DelayedEntityProcessingSystem extends EntitySystem {

	/** The time until an entity should be processed. */
	private float delay;
	/**	If the system is running and counting down delays. */
	private boolean running;
	/** The countdown, accumulates world deltas. */
	private float acc;


	/**
	 * Creates a new DelayedEntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match against entities
	 */
	public DelayedEntityProcessingSystem(Aspect aspect) {
		super(aspect);
	}



	@Override
	protected final void processEntities(ImmutableBag<Entity> entities) {
		Object[] array = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			Entity entity = (Entity)array[i];
			processDelta(entity, acc);
			float remaining = getRemainingDelay(entity);
			if(remaining <= 0) {
				processExpired(entity);
			} else {
				offerDelay(remaining);
			}
		}
		
		if (getActives().size() == 0) {
			stop();
		}
	}


	@Override
	protected void inserted(Entity e) {
		float remainingDelay = getRemainingDelay(e);
		processDelta(e, -acc);
		if(remainingDelay > 0) {
			offerDelay(remainingDelay);
		}
	}
	
	/**
	 * Return the delay until this entity should be processed.
	 * 
	 * @param e
	 *			entity
	 *
	 * @return delay
	 */
	protected abstract float getRemainingDelay(Entity e);


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
	 * Process a entity this system is interested in.
	 * <p>
	 * Substract the accumulatedDelta from the entities defined delay.
	 * </p>
	 * 
	 * @param e
	 *			the entity to process
	 * @param accumulatedDelta
	 *			the delta time since this system was last executed
	 */
	protected abstract void processDelta(Entity e, float accumulatedDelta);


	protected abstract void processExpired(Entity e);

	/**
	 * Start processing of entities after a certain amount of delta time.
	 * <p>
	 * Cancels current delayed run and starts a new one.
	 * </p>
	 * 
	 * @param delay
	 *			time delay until processing starts
	 */
	public void restart(float delay) {
		this.delay = delay;
		this.acc = 0;
		running = true;
	}
	
	/**
	 * Restarts the system only if the delay offered is shorter than the time
	 * that the system is currently scheduled to execute at.
	 * <p>
	 * If the system is already stopped (not running) then the offered delay
	 * will be used to restart the system with no matter its value.
	 * </p><p>
	 * If the system is already counting down, and the offered delay is  larger
	 * than the time remaining, the system will ignore it. If the offered delay
	 * is shorter than the time remaining, the system will restart itself to
	 * run at the offered delay.
	 * </p>
	 *
	 * @param offeredDelay
	 *			delay to offer
	 */
	public void offerDelay(float offeredDelay) {
		if(!running || offeredDelay < getRemainingTimeUntilProcessing()) {
			restart(offeredDelay);
		}
	}
	
	/**
	 * Get the initial delay that the system was ordered to process entities
	 * after.
	 * 
	 * @return the originally set delay
	 */
	public float getInitialTimeDelay() {
		return delay;
	}
	
	/**
	 * Get the time until the system is scheduled to run at.
	 * <p>
	 * Returns zero (0) if the system is not running.
	 * Use {@link #isRunning() isRunning()} before checking this value.
	 * </p>
	 *
	 * @return time when system will run at
	 */
	public float getRemainingTimeUntilProcessing() {
		if(running) {
			return delay-acc;
		}
		return 0;
	}
	
	/**
	 * Check if the system is counting down towards processing.
	 * 
	 * @return {@code true} if it's counting down, false if it's not running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the system from running, aborts current countdown.
	 * <p>
	 * Call offerDelay or restart to run it again.
	 * </p>
	 */
	public void stop() {
		this.running = false;
		this.acc = 0;
	}

}
