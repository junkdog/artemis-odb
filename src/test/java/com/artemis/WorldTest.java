package com.artemis;

import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Mapper;
import com.artemis.component.ComponentX;
import com.artemis.systems.EntityProcessingSystem;

public class WorldTest
{
	private World world;

	@Before
	public void setUp() throws Exception
	{
		world = new World();
	}

	@Test
	public void access_component_after_deletion_in_previous_system()
	{
		world.setSystem(new SystemA());
		world.setSystem(new SystemB());
		world.initialize();
		
		Entity e = world.createEntity();
		e.addComponent(new ComponentX());
		e.addToWorld();
		
		world.process();
	}

	static class SystemA extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemA()
		{
			super(Aspect.getAspectForAll(ComponentX.class));
		}

		@Override
		protected void process(Entity e)
		{
			e.removeComponent(ComponentX.class);
			e.changedInWorld();
		}
	}

	static class SystemB extends EntityProcessingSystem
	{
		@Mapper
		ComponentMapper<ComponentX> xm;

		@SuppressWarnings("unchecked")
		public SystemB()
		{
			super(Aspect.getAspectForAll(ComponentX.class));
		}

		@Override
		protected void process(Entity e)
		{
			ComponentX x = xm.get(e);
			System.out.println(x.text);
		}
	}
}
