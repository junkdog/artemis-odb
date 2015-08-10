package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class SystemB extends EntityProcessingSystem {
	ComponentMapper<ComponentX> xm;

	@SuppressWarnings("unchecked")
	public SystemB()
	{
		super(Aspect.getAspectForAll(ComponentX.class));
	}

	@Override
	protected void process(Entity e)
	{
		xm.get(e);
	}
}