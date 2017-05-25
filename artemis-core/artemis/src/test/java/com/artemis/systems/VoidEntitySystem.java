package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;

public abstract class VoidEntitySystem extends EntitySystem {

	public VoidEntitySystem() {
		super(Aspect.exclude()); // exclude is only semantic here...
	}

	/**
	 * Override to implement behavior when this system is called by the world.
	 */
	protected abstract void processSystem();
}
