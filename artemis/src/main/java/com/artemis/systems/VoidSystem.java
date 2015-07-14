package com.artemis.systems;

import com.artemis.BaseSystem;

/**
 * This system processes no entities, but is still invoked once per tick.
 * <p>
 * You can use this system if you need to execute some game logic and not have
 * to concern yourself about aspects or entities.
 * </p>
 */
public abstract class VoidSystem extends BaseSystem {
	/**
	 * Override to implement behavior when this system is called by the world.
	 */
	protected abstract void processSystem();
}
