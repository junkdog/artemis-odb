package com.artemis;

import com.artemis.systems.DelayedIteratingSystem;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class DelayedIteratingSystemTest
{
	protected LinkedList<Entity> entitiesOrdered;
	private World world;
	private ExpirationSystem es;

	@Before
	public void setUp() {
		world = new World(new WorldConfiguration()
				.setSystem(new ExpirationSystem()));
		world.inject(this);
		entitiesOrdered = new LinkedList<Entity>();
	}

	@Test
	public void constant_firing()
	{
		assertEquals(0, entitiesOrdered.size());

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
		assertEquals(0, es.getSubscription().getEntities().size());
	}

	@Test
	public void constant_firing_smaller_deltas()
	{
		assertEquals(0, entitiesOrdered.size());

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
		assertEquals(0, es.getSubscription().getEntities().size());
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

	public static class Expiration extends Component {
		public float delay;

		public Expiration() {}

		public Expiration(final float delay) {
			this.delay = delay;
		}
	}

	public class ExpirationSystem extends DelayedIteratingSystem
	{
		public int expiredLastRound;

		ComponentMapper<Expiration> em;

		@SuppressWarnings("unchecked")
		public ExpirationSystem() {
			super(Aspect.all(Expiration.class));
		}

		@Override
		protected float getRemainingDelay(final int e) {
			return em.get(e).delay;
		}

		@Override
		protected void processDelta(final int e, final float accumulatedDelta) {
			final Expiration expires = em.get(e);
			expires.delay -= accumulatedDelta;
		}

		@Override
		protected void processExpired(final int e) {
			expiredLastRound++;
			assertEquals(e, entitiesOrdered.removeFirst().id);
			world.delete(e);
		}

		@Override
		protected void begin() {
			expiredLastRound = 0;
		}
	}
}
