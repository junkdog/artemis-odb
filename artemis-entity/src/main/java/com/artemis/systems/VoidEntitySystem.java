package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;

/**
 * @Deprecated Why are you even using this!?
 */
@Deprecated
public abstract class VoidEntitySystem<T extends Entity> extends EntitySystem<T> {

	public VoidEntitySystem() {
		super(Aspect.exclude()); // exclude is only semantic here...
	}

	/**
	 * Override to implement behavior when this system is called by the world.
	 */
	protected abstract void processSystem();
}
