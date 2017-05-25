package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;


/**
 * A system that processes entities at a interval in milliseconds.
 * <p>
 * A typical usage would be a collision system or physics system.
 * </p>
 * 
 * @author Arni Arent
 */
public abstract class IntervalEntitySystem extends EntitySystem {

	/** Accumulated delta to keep track of interval. */
	protected float acc;
	/** How long to wait between updates. */
	private final float interval;

	private float intervalDelta;

	/**
	 * Creates a new IntervalEntitySystem.
	 * @param aspect
	 *			the aspect to match entities
	 * @param interval
	 *			the interval at which the system processes
	 */
	public IntervalEntitySystem(Aspect.Builder aspect, float interval) {
		super(aspect);
		this.interval = interval;
	}

	@Override
	protected boolean checkProcessing() {
		if (intervalDelta > 0 && acc == 0)
			intervalDelta = 0;

		acc += getTimeDelta();
		if(acc >= interval) {
			intervalDelta = acc;
			acc = 0;

			return true;
		}
		return false;
	}

	/**
	 * Gets the actual delta since this system was last processed.
	 * 
	 * @return Time passed since last process round.
	 */
	protected float getIntervalDelta() {
		return intervalDelta + acc;
	}
	
	protected float getTimeDelta() {
		return world.getDelta();
	}

}
