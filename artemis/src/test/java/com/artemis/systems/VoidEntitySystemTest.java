package com.artemis.systems;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class VoidEntitySystemTest {

	@Test
	public void test_system_is_initialized() {
		VoidSys vs = new VoidSys();
		World world = new World(new WorldConfiguration()
				.setSystem(vs));

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
