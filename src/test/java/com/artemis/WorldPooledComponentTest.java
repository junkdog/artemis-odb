package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.artemis.component.ReusedComponent;
import com.artemis.systems.EntityProcessingSystem;

public class WorldPooledComponentTest
{
	private World world;

	@Before
	public void setUp() throws Exception
	{
		world = new World();
	}

	@Test
	public void pooled_component_reuse_with_deleted_entities()
	{
		world.setSystem(new SystemComponentEntityRemover());
		world.initialize();

		runWorld();
	}
	
	@Test
	public void pooled_component_reuse_with_removed_components()
	{
		world.setSystem(new SystemComponentPooledRemover());
		world.initialize();
		
		runWorld();
	}

	private void runWorld()
	{
		Set<Integer> hashes = new HashSet<Integer>();
		hashes.add(createEntity());
		hashes.add(createEntity());
		world.process();
		world.process();
		hashes.add(createEntity());
		world.process();
		hashes.add(createEntity());
		world.process();
		hashes.add(createEntity());
		world.process();
		hashes.add(createEntity());
		hashes.add(createEntity());
		hashes.add(createEntity());
		world.process();
		hashes.add(createEntity());
		world.process();
		
		assertEquals("Contents: " + hashes, 3, hashes.size());
	}
	
	private int createEntity()
	{
		Entity e = world.createEntity();
		ReusedComponent component = e.createComponent(ReusedComponent.class);
		e.addToWorld();
		int hash = System.identityHashCode(component);
		return hash;
	}
	
	static class SystemComponentEntityRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentEntityRemover()
		{
			super(Aspect.getAspectForAll(ReusedComponent.class));
		}

		@Override
		protected void process(Entity e)
		{
			e.deleteFromWorld();
		}
	}
	
	static class SystemComponentPooledRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentPooledRemover()
		{
			super(Aspect.getAspectForAll(ReusedComponent.class));
		}
		
		@Override
		protected void process(Entity e)
		{
			e.removeComponent(ReusedComponent.class);
		}
	}
}
