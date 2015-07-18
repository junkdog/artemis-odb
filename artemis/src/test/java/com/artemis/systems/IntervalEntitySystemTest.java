package com.artemis.systems;

import static org.junit.Assert.*;

import com.artemis.WorldConfiguration;
import org.junit.Test;

import com.artemis.Aspect;
import com.artemis.World;
import com.artemis.component.ComponentX;

@SuppressWarnings("static-method")
public class IntervalEntitySystemTest {
	
	private static final float ACC = 0.0001f;

	@Test
	public void test_interval_delta() {
		IntervalSystem es = new IntervalSystem();
		World world = new World(new WorldConfiguration()
				.setSystem(es));

		world.delta = 1.1f;
		world.process();
		assertEquals(1.1, es.getIntervalDelta(), ACC);
		
		world.delta = 0.95f;
		world.process();
		assertEquals(0.95, es.getIntervalDelta(), ACC);
	}

	private static class IntervalSystem extends IntervalEntitySystem {

		@SuppressWarnings("unchecked")
		public IntervalSystem() {
			super(Aspect.all(ComponentX.class), 1);
		}

		@Override
		protected void processSystem() {}
	}
}
