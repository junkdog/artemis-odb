package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import com.artemis.component.CountingPooledComponent;
import org.junit.Test;

import com.artemis.component.ReusedComponent;
import com.artemis.systems.EntityProcessingSystem;

public class WorldPooledComponentTest
{

	@Test // FIXME, the +1 shouldn't be necessary but there is some delay or something when deleting.
	public void pooled_component_reuse_with_deleted_entities()
	{
		World world = new World(new WorldConfiguration()
				.setSystem(new SystemComponentEntityRemover()));

		Set<Integer> hashes = runWorld(world);
		assertEquals("Contents: " + hashes, 3 + 1, hashes.size());
	}

	private Set<Integer> runWorld(World world)
	{
		Set<Integer> hashes = new HashSet<Integer>();
		hashes.add(createEntity(world));
		hashes.add(createEntity(world));
		world.process();
		hashes.add(createEntity(world));
		world.process();
		hashes.add(createEntity(world));
		world.process();
		hashes.add(createEntity(world));
		world.process();
		hashes.add(createEntity(world));
		hashes.add(createEntity(world));
		hashes.add(createEntity(world));
		world.process();
		world.process();
		hashes.add(createEntity(world));
		world.process();
		
		return hashes;
	}

	@Test
	public void creating_pooled_components_returns_old_to_pool() {
		World world = new World();
		int e = world.createEntity();
		CountingPooledComponent cpc1 = EntityHelper.edit(world, e).create(CountingPooledComponent.class);
		world.process();

		EntityHelper.edit(world, e).create(CountingPooledComponent.class);
		world.process();

		assertEquals(cpc1, EntityHelper.edit(world, e).create(CountingPooledComponent.class));
	}
	
	private int createEntity(World world)
	{
		int e = world.createEntity();
		ReusedComponent component = EntityHelper.edit(world, e).create(ReusedComponent.class);
		return e;
	}
	
	static class SystemComponentEntityRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentEntityRemover()
		{
			super(Aspect.all(ReusedComponent.class));
		}

		@Override
		protected void process(int e)
		{
			world.deleteEntity(e);
		}
	}
	
	static class SystemComponentPooledRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentPooledRemover()
		{
			super(Aspect.all(ReusedComponent.class));
		}
		
		@Override
		protected void process(int e)
		{
			EntityHelper.edit(world, e).remove(ReusedComponent.class);
		}
	}
}
