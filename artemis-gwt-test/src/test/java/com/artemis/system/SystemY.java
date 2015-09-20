package com.artemis.system;

import junit.framework.Assert;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

class SystemY extends EntityProcessingSystem {
	ComponentMapper<ComponentY> ym;
	
	@SuppressWarnings("unchecked")
	public SystemY()
	{
		super(Aspect.all(ComponentY.class));
	}
	
	@Override
	protected void process(int e)
	{
		Assert.assertNotNull(ym);
		ym.get(e);
	}
}