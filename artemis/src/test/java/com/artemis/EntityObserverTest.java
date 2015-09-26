package com.artemis;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class EntityObserverTest {

	@Test
	public void ensure_systems_with_entityobserver_receive_events() {

		class TestSystem extends Manager {

			public int added=0;

			@Override
			public void added(Entity e) {
				added++;
			}
		}

		TestSystem system = new TestSystem();
		World world = new World(new WorldConfiguration()
				.setSystem(system));
		world.createEntity();
		world.process();

		Assert.assertEquals(1,system.added);
	}

	@Test
	public void ensure_systems_without_entityobserver_do_not_cause_exceptions() {
		class TestSystem extends BaseSystem  {
			@Override
			protected void processSystem() {
			}
		}

		TestSystem system = new TestSystem();
		World world = new World(new WorldConfiguration()
				.setSystem(system));
		world.createEntity();
		world.process();
	}
}