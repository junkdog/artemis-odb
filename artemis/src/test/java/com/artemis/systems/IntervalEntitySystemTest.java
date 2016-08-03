package com.artemis.systems;

import static com.artemis.Aspect.all;
import static org.junit.Assert.*;

import com.artemis.WorldConfiguration;
import org.junit.Test;

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

	@Test
	public void test_interval_delta_when_world_step_is_bigger_than_system_interval() {
		IntervalSystem2 intervalSystem = new IntervalSystem2();
		World world = new World(new WorldConfiguration()
			.setSystem(intervalSystem));

		float worldTotalTime=0;
		float intervalSystemTotalIntervalTime=0;
		for (int i = 0; i < 4; i++) {
			world.delta = 2f;
			worldTotalTime += world.delta;
			world.process();
			// intervalSystem has 1 second interval
			// so it will run processing round on each world step
			intervalSystemTotalIntervalTime +=intervalSystem.getIntervalDelta();
		}

		assertEquals(worldTotalTime, 2 * 4, ACC);
		assertEquals(intervalSystemTotalIntervalTime, 2 * 4, ACC);
	}

	private static class IntervalSystem extends IntervalEntitySystem {

		@SuppressWarnings("unchecked")
		public IntervalSystem() {
			super(all(ComponentX.class), 1);
		}

		@Override
		protected void processSystem() {}
	}

	private static class IntervalSystem2 extends IntervalEntitySystem {

		@SuppressWarnings("unchecked")
		public IntervalSystem2() {
			super(all(ComponentX.class), 1);
		}

		@Override
		protected void processSystem() {}
	}
}
