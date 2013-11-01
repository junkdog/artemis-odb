package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Mapper;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.DelayedEntityProcessingSystem;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

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
	public void delayed_entity_procesing_ensure_entities_processed()
	{
		ExpirationSystem es = new ExpirationSystem();
		world.setSystem(es);
		world.initialize();
		
		Entity e1 = createEntity();
		
		world.setDelta(0.5f);
		world.process();
		assertEquals(0, es.expiredLastRound);
		
		Entity e2 = createEntity();
		
		world.setDelta(0.75f);
		world.process();
		assertEquals(1, es.expiredLastRound);
		assertEquals(0.25f, es.deltas.get(e2.getId()), 0.01f);
	}

	private Entity createEntity()
	{
		Entity e = world.createEntity();
		e.createComponent(ComponentY.class);
		e.addToWorld();
		return e;
	}
	
	// @Test
	public void system_adding_system_in_initialize()
	{
		world.setSystem(new SystemSpawner());
		world.setSystem(new SystemB());
		world.initialize();
		
		createEntity();
		
		world.process();
		ImmutableBag<EntitySystem> systems = world.getSystems();
		assertEquals(systems.toString(), 3, systems.size());
		assertEquals(SystemSpawner.class, systems.get(0).getClass());
		// FIXME: there should be a machanism for ordering systems
		// if they are to be added into an already initialized world
		// assertEquals(SystemY.class, systems.get(1).getClass());
		// assertEquals(SystemB.class, systems.get(2).getClass());
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
	
	static class ExpirationSystem extends DelayedEntityProcessingSystem
	{
		// don't do this IRL
		private Bag<Float> deltas = new Bag<Float>();
		int expiredLastRound;

		@SuppressWarnings("unchecked")
		public ExpirationSystem() {
			super(Aspect.getAspectForAll(ComponentY.class));
		}
		
		@Override
		protected void inserted(Entity e) {
			deltas.set(e.getId(), 1f);
			super.inserted(e);
		}
		
		@Override
		protected float getRemainingDelay(Entity e) {
			return deltas.get(e.getId());
		}

		@Override
		protected void processDelta(Entity e, float accumulatedDelta) {
			float remaining = deltas.get(e.getId());
			remaining -=  accumulatedDelta;
			offerDelay(remaining);
			deltas.set(e.getId(), remaining);
		}

		@Override
		protected void processExpired(Entity e) {
			expiredLastRound++;
			deltas.set(e.getId(), null);
		}
		
		@Override
		protected void begin() {
			expiredLastRound = 0;
		}
	}
}
