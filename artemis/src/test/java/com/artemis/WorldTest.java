package com.artemis;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
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
			if (i == 0) e.edit().add(new ComponentX());
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
		e.edit().create(ComponentX.class);
		
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
		world.delta = 0;
		world.process();
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
		e.edit().create(ComponentY.class);
		return e;
	}

	static class SystemComponentXRemover extends EntityProcessingSystem
	{
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

	@Wire
	static class SystemB extends EntityProcessingSystem
	{
		ComponentMapper<ComponentX> xm;

		@SuppressWarnings("unchecked")
		public SystemB()
		{
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void process(Entity e)
		{
			xm.get(e);
		}
	}
	
	@Wire
	static class SystemY extends EntityProcessingSystem
	{
		ComponentMapper<ComponentY> ym;
		
		@SuppressWarnings("unchecked")
		public SystemY()
		{
			super(Aspect.all(ComponentY.class));
		}
		
		@Override
		protected void process(Entity e)
		{
			Assert.assertNotNull(ym);
			ym.get(e);
		}
	}
	
	static class ExpirationSystem extends DelayedEntityProcessingSystem
	{
		// don't do this IRL
		private Bag<Float> deltas = new Bag<Float>();
		int expiredLastRound;

		@SuppressWarnings("unchecked")
		public ExpirationSystem() {
			super(Aspect.all(ComponentY.class));
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
