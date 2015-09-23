package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.component.ComponentX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("static-method")
public class IntervalSystemTest {
	
	private static final float ACC = 0.0001f;

	@Test
	public void test_interval_delta() {
		IntervalSystemCollision es = new IntervalSystemCollision();
		World world = new World(new WorldConfiguration()
				.setSystem(es));

		world.delta = 1.1f;
		world.process();
		assertEquals(1.1, es.getIntervalDelta(), ACC);
		
		world.delta = 0.95f;
		world.process();
		assertEquals(0.95, es.getIntervalDelta(), ACC);
	}

	private static class IntervalSystemCollision extends IntervalSystem {

		public IntervalSystemCollision() {
			super(Aspect.all(ComponentX.class), 1);
		}

		@Override
		protected void processSystem() {}
	}
}
