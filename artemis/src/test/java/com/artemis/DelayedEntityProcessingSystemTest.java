package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedEntityProcessingSystem;

public class DelayedEntityProcessingSystemTest
{
	protected LinkedList<Entity> entitiesOrdered;
	private World world;

	@Before
	public void setUp() throws Exception
	{
		world = new World();
		entitiesOrdered = new LinkedList<Entity>();
	}

	@Test
	public void constant_firing()
	{
		assertEquals(0, entitiesOrdered.size());

		final ExpirationSystem es = new ExpirationSystem();
		world.setSystem(es);
		world.initialize();

		createEntity();

		world.setDelta(0.21f);
		world.process();
		assertEquals(0, es.expiredLastRound);

		createEntity();

		world.setDelta(0.21f);
		world.process();
		assertEquals(0, es.expiredLastRound);

		createEntity();

		world.setDelta(0.21f);
		world.process();
		assertEquals(0, es.expiredLastRound);

		createEntity();

		world.setDelta(0.21f);
		world.process();

		assertEquals(0, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.75f);
		world.process();
		// assertEquals(0, es.expiredLastRound); // begin() isn't run unless the system is processed
		assertEquals(0, entitiesOrdered.size());
		assertEquals(0, es.getActives().size());
	}

	@Test
	public void constant_firing_smaller_deltas()
	{
		assertEquals(0, entitiesOrdered.size());

		final ExpirationSystem es = new ExpirationSystem();
		world.setSystem(es);
		world.initialize();

		createEntity();

		step200ms(es);

		createEntity();

		step200ms(es);

		createEntity();

		step200ms(es);

		createEntity();

		step200ms(es);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.21f);
		world.process();
		assertEquals(1, es.expiredLastRound);

		world.setDelta(0.75f);
		world.process();
//		assertEquals(0, es.expiredLastRound); // begin() isn't run unless the system is processed
		assertEquals(0, es.getActives().size());
		assertEquals(0, entitiesOrdered.size());
	}

	private void step200ms(final ExpirationSystem es) {
		for (int i = 0; i < 10; ++i) {
			world.setDelta(0.02f);
			world.process();
			assertEquals(0, es.expiredLastRound);
		}
	}

	private Entity createEntity()
	{
		final Entity e = world.createEntity();
		e.edit().add(new Expiration(1f));

		entitiesOrdered.addLast(e);
		return e;
	}

	public class Expiration extends Component {
		public float delay;

		/**
		 * @param delay
		 *			in seconds
		 */
		public Expiration(final float delay) {
			this.delay = delay;
		}
	}

	@Wire
	public class ExpirationSystem extends DelayedEntityProcessingSystem
	{
		public int expiredLastRound;

		ComponentMapper<Expiration> em;

		@SuppressWarnings("unchecked")
		public ExpirationSystem() {
			super(Aspect.all(Expiration.class));
		}

		@Override
		protected float getRemainingDelay(final Entity e) {
			return em.get(e).delay;
		}

		@Override
		protected void processDelta(final Entity e, final float accumulatedDelta) {
			final Expiration expires = em.get(e);
			expires.delay -= accumulatedDelta;
		}

		@Override
		protected void processExpired(final Entity e) {
			expiredLastRound++;
			assertEquals(e, entitiesOrdered.removeFirst());
			e.deleteFromWorld();
		}

		@Override
		protected void begin() {
			expiredLastRound = 0;
		}
	}
}
