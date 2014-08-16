package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class OptimizedSystem extends EntityProcessingSystem {

	public OptimizedSystem() {
		super(Aspect.getEmpty());
	}

	@Override
	protected void process(Entity e) {}

}
