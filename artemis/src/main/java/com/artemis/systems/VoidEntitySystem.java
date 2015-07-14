package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;

/**
 * This system has an empty aspect so it processes no entities, but it still
 * gets invoked.
 * <p>
 * You can use this system if you need to execute some game logic and not have
 * to concern yourself about aspects or entities.
 * </p>
 * 
 * @author Arni Arent
 *
 *
 *
 * @deprecated Replaced by {@link VoidSystem}
 */
@Deprecated
public abstract class VoidEntitySystem extends EntitySystem {

	public VoidEntitySystem() {
		super(Aspect.exclude()); // exclude is only semantic here...
	}

	/**
	 * Override to implement behavior when this system is called by the world.
	 */
	protected abstract void processSystem();
}
