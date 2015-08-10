package com.artemis.system;

import junit.framework.Assert;

import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentY;
import com.artemis.systems.VoidEntitySystem;

@Wire 
public class SystemSpawner extends VoidEntitySystem {
	public ComponentMapper<ComponentY> ym;
	
	@Override
	protected void initialize()
	{
		world.setSystem(new SystemY());
	}
	
	@Override
	protected void processSystem()
	{
		Assert.assertNotNull(ym);
	}
}