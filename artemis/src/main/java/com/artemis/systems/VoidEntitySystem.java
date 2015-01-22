package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;


/**
 * This system has an empty aspect so it processes no entities, but it still
 * gets invoked.
 * <p>
 * You can use this system if you need to execute some game logic and not have
 * to concern yourself about aspects or entities.
 * </p>
 * 
 * @author Arni Arent
 */
public abstract class VoidEntitySystem extends EntitySystem {

	/**
	 * Creates a new VoidEntitySystem.
	 */
	public VoidEntitySystem() {
		super(null);
	}

	@Override
	protected final void processEntities(IntBag entities) {
		processSystem();
	}

	/**
	 * Override to implement behavior when this system is called by the world.
	 */
	protected abstract void processSystem();
}
