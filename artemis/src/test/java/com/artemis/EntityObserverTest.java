package com.artemis;

import com.artemis.utils.IntBag;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class EntityObserverTest {

	@Test
	public void ensure_systems_with_entityobserver_receive_events() {

		class TestSystem extends BaseSystem implements EntityObserver {

			public int added=0;

			@Override
			protected void processSystem() {
			}

			@Override
			public void added(int entityId) {
			}

			@Override
			public void added(IntBag entities) {
				added++;
			}

			@Override
			public void changed(int entityId) {
			}

			@Override
			public void changed(IntBag entities) {
			}

			@Override
			public void deleted(int entityId) {
			}

			@Override
			public void deleted(IntBag entities) {
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