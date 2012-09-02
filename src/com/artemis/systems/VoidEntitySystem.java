package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;

public abstract class VoidEntitySystem extends EntitySystem {

	public VoidEntitySystem() {
		super(Aspect.getEmpty());
	}

	@Override
	protected final void processEntities(ImmutableBag<Entity> entities) {
		processSystem();
	}
	
	protected abstract void processSystem();

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
