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
	@Test
	public void pooled_component_reuse_with_deleted_entities() {
		EntityWorld world = new EntityWorld(new WorldConfiguration()
				.setSystem(new SystemComponentEntityRemover()));

		Set<Integer> hashes = runWorld(world);
		assertEquals("Contents: " + hashes, 3, hashes.size());
	}

	private Set<Integer> runWorld(EntityWorld world) {
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
		EntityWorld w = new EntityWorld();
		Entity e = w.createEntity();
		CountingPooledComponent cpc1 = e.edit().create(CountingPooledComponent.class);
		w.process();

		e.edit().create(CountingPooledComponent.class);
		w.process();

		assertEquals(cpc1, e.edit().create(CountingPooledComponent.class));
	}

	private int createEntity(EntityWorld world)
	{
		Entity e = world.createEntity();
		ReusedComponent component = e.edit().create(ReusedComponent.class);
		return e.hashCode();
	}

	static class SystemComponentEntityRemover extends EntityProcessingSystem
	{
		@SuppressWarnings("unchecked")
		public SystemComponentEntityRemover()
		{
			super(Aspect.all(ReusedComponent.class));
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
			super(Aspect.all(ReusedComponent.class));
		}
		
		@Override
		protected void process(Entity e)
		{
			e.edit().remove(ReusedComponent.class);
		}
	}
}
