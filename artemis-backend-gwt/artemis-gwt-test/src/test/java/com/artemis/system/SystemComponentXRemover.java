package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.component.ComponentX;
import com.artemis.systems.EntityProcessingSystem;

public class SystemComponentXRemover extends EntityProcessingSystem {
	@SuppressWarnings("unchecked")
	public SystemComponentXRemover()
	{
		super(Aspect.all(ComponentX.class));
	}

	@Override
	protected void process(Entity e)
	{
		e.edit().remove(ComponentX.class);
	}
}