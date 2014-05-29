package com.artemis.system;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.component.Position;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class PositionSystem extends EntityProcessingSystem {

	ComponentMapper<Position> positionMapper;
	
	@SuppressWarnings("unchecked")
	public PositionSystem() {
		super(Aspect.getAspectForAll(Position.class));
	}

	@Override
	protected void process(Entity e) {
		Position pos = positionMapper.get(e);
		pos.x(pos.x() + .1f % 100000);
		pos.y(pos.y() - 0.1f % 100000);
	}
}
