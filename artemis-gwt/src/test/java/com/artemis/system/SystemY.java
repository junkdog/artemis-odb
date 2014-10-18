package com.artemis.system;

import junit.framework.Assert;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

@Wire class SystemY extends EntityProcessingSystem {
	ComponentMapper<ComponentY> ym;
	
	@SuppressWarnings("unchecked")
	public SystemY()
	{
		super(Aspect.getAspectForAll(ComponentY.class));
	}
	
	@Override
	protected void process(Entity e)
	{
		Assert.assertNotNull(ym);
		ym.get(e);
	}
}