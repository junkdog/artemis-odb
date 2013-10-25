package com.artemis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Mapper;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
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
		world.setSystem(new SystemComponentXRemover());
		world.setSystem(new SystemB());
		world.initialize();
		
		Entity e = world.createEntity();
		e.createComponent(ComponentX.class);
		e.addToWorld();
		
		world.process();
	}

	static class SystemComponentXRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentXRemover()
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
		}
	}
	
	static class SystemY extends EntityProcessingSystem
	{
		@Mapper
		ComponentMapper<ComponentY> ym;
		
		@SuppressWarnings("unchecked")
		public SystemY()
		{
			super(Aspect.getAspectForAll(ComponentY.class));
		}
		
		@Override
		protected void process(Entity e)
		{
			ComponentY y = ym.get(e);
		}
	}
}
