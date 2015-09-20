package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.component.ComponentX;
import com.artemis.systems.EntityProcessingSystem;

public class SystemB extends EntityProcessingSystem {
	ComponentMapper<ComponentX> xm;

	@SuppressWarnings("unchecked")
	public SystemB()
	{
		super(Aspect.all(ComponentX.class));
	}

	@Override
	protected void process(int e)
	{
		xm.get(e);
	}
}