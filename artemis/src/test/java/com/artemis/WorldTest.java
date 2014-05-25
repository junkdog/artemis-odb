package com.artemis;

import static org.junit.Assert.assertEquals;

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

public class WorldTest
{
	private World world;

	@Before
	public void setUp() throws Exception
	{
		world = new World();
	}
	
	@Test
	public void get_component_should_not_throw_exception()
	{
		world = new World();
		world.initialize();

		for (int i = 0; i < 100; i++) {
			Entity e = world.createEntity();
			if (i == 0) e.addComponent(new ComponentX());
			e.addToWorld();
		}

		world.process();

		for (int i = 0; i < 100; i++) {
			Entity e = world.getEntity(i);
			e.getComponent(ComponentX.class);
		}
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
		assertEquals(1, es.getActives().size());
		
		world.setDelta(0.5f);
		world.process();
		
		assertEquals(1, es.expiredLastRound);
		
		world.process();
		assertEquals(0, es.getActives().size());
	}

	private Entity createEntity()
	{
		Entity e = world.createEntity();
		e.createComponent(ComponentY.class);
		e.addToWorld();
		return e;
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
			e.deleteFromWorld();
		}
		
		@Override
		protected void begin() {
			expiredLastRound = 0;
		}
	}
}
