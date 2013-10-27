package com.artemis;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Mapper;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;

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
	
	@Test
	public void system_adding_system_in_initialize()
	{
		world.setSystem(new SystemSpawner());
		world.initialize();
		
		Entity e = world.createEntity();
		e.createComponent(ComponentY.class);
		e.addToWorld();
		
		world.process();
		
		assertEquals(2, world.getSystems().size());
		assertEquals(1, world.getSystem(SystemY.class).getActives().size());
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
			Assert.assertNotNull(ym);
			ComponentY y = ym.get(e);
			System.out.println("Running " + getClass());
		}
	}
	
	static class SystemSpawner extends VoidEntitySystem
	{
		@Mapper
		ComponentMapper<ComponentY> ym;
		
		@Override
		protected void initialize()
		{
			world.setSystem(new SystemY());
		}
		
		@Override
		protected void processSystem()
		{
			System.out.println("Running " + getClass());
			Assert.assertNotNull(ym);
		}
	}
}
