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


	protected float acc;

	private final float interval;


	/**
	 * Creates a new IntervalEntitySystem.
	 * @param aspect
	 *			the aspect to match entities
	 * @param interval
	 *			the interval at which the system processes
	 */
	public IntervalEntitySystem(Aspect aspect, float interval) {
		super(aspect);
		this.interval = interval;
	}


	@Override
	protected boolean checkProcessing() {
		acc += world.getDelta();
		if(acc >= interval) {
			acc -= interval;
			return true;
		}
		return false;
	}

}
