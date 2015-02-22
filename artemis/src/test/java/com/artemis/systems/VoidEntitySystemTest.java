package com.artemis.systems;

import com.artemis.World;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class VoidEntitySystemTest {

	@Test
	public void test_system_is_initialized() {
		World world = new World();
		VoidSys vs = world.setSystem(new VoidSys());
		world.initialize();

		assertTrue(vs.initialized);
	}

	public static class VoidSys extends VoidEntitySystem {
		boolean initialized;

		@Override
		protected void initialize() {
			initialized = true;
		}

		@Override
		protected void processSystem() {

		}
	}
}
