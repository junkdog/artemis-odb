package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
	public void pooled_component_reuse()
	{
		world.initialize();

		int hash1 = createEntity();
		int hash2 = createEntity();
		world.process();
		int hash3 = createEntity();
		world.process();
		int hash4 = createEntity();
		world.process();
		int hash5 = createEntity();
		world.process();
		
		assertEquals(hash1, hash3);
		assertNotEquals(hash1, hash2);
		assertEquals(hash1, hash4);
		assertEquals(hash1, hash5);
	}

	private int createEntity()
	{
		Entity e = world.createEntity();
		ReusedComponent component = e.addPooledComponent(ReusedComponent.class);
		e.addToWorld();
		return System.identityHashCode(component);
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
			e.deleteFromWorld();
		}
	}
}
