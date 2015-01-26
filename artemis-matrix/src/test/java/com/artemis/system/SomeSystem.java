package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.component.ExtPosition;
import com.artemis.component.Position;
import com.artemis.systems.EntityProcessingSystem;

public class SomeSystem extends EntityProcessingSystem {

	public SomeSystem(Aspect aspect) {
		super(Aspect.getAspectForAll(Position.class, ExtPosition.class));
	}

	@Override
	protected void process(Entity e) {}
}
