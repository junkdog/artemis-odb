package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.component.Position;
import com.artemis.component.Velocity;
import com.artemis.systems.EntityProcessingSystem;

public class AnotherSystem extends EntityProcessingSystem {

	public AnotherSystem(Aspect aspect) {
		super(Aspect.getAspectForAll(Position.class, Velocity.class));
	}

	@Override
	protected void process(Entity e) {}
}
