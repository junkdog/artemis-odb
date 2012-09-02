package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;


/**
 * A system that processes entities at a interval in milliseconds.
 * A typical usage would be a collision system or physics system.
 * 
 * @author Arni Arent
 *
 */
public abstract class IntervalEntitySystem extends EntitySystem {
	private float acc;
	private float interval;

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
