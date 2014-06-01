package com.artemis.system;

import org.openjdk.jmh.logic.BlackHole;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.component.PlainPosition;
import com.artemis.systems.EntityProcessingSystem;

public class BaselinePositionSystem extends EntityProcessingSystem {
	
	BlackHole voidness = new BlackHole();
	
	@SuppressWarnings("unchecked")
	public BaselinePositionSystem() {
		super(Aspect.getAspectForAll(PlainPosition.class));
	}

	@Override
	protected void process(Entity e) {
		voidness.consume(e);
	}
}
