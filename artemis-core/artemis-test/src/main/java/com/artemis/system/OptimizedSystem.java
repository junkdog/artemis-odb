package com.artemis.system;

import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class OptimizedSystem extends EntityProcessingSystem<Entity> {

	public OptimizedSystem() {
		super(null);
	}

	@Override
	protected void process(Entity e) {}

}
