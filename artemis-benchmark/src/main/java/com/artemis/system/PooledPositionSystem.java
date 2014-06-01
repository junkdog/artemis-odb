package com.artemis.system;

import org.openjdk.jmh.logic.BlackHole;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.component.PooledPosition;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class PooledPositionSystem extends EntityProcessingSystem {

	BlackHole voidness = new BlackHole();
	ComponentMapper<PooledPosition> positionMapper;
	
	@SuppressWarnings("unchecked")
	public PooledPositionSystem() {
		super(Aspect.getAspectForAll(PooledPosition.class));
	}

	@Override
	protected void process(Entity e) {
		PooledPosition pos = positionMapper.get(e);
		pos.x += 0.1f % 100000;
		pos.y -= 0.1f % 100000;
		
		voidness.consume(e);
	}
}
