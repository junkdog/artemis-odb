package com.artemis;

import com.artemis.systems.EntityProcessingSystem;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Snorre E. Brekke
 */
public class Issue357SystemTest {
	@Test
	public void test_two_systems_in_world_delete_during_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithDelete.class)
				.setSystem(AnyOldeBaseSystem.class));
		world.createEntity().edit().create(TestComponent.class);
		world.process();

		world.createEntity().edit().create(TestComponent.class);
		world.process(); //This test fails in 0.13.0!

	}

	@Test
	public void test_one_system_in_world_delete_during_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithDelete.class));
		world.createEntity().edit().create(TestComponent.class);
		world.process();

		world.createEntity().edit().create(TestComponent.class);
		world.process();
		//This test is ok i 0.13.0
	}

	@Test
	public void test_two_systems_in_world_delete_after_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithoutDelete.class)
				.setSystem(AnyOldeBaseSystem.class));
		Entity entity = world.createEntity();
		entity.edit().create(TestComponent.class);
		world.process();
		entity.deleteFromWorld();

		Entity entity2 = world.createEntity();
		entity2.edit().create(TestComponent.class);
		world.process();
		entity2.deleteFromWorld();
		world.process();
		//This test is ok i 0.13.0
	}

	@Test
	public void test_two_systems_in_world_delete_before_process() throws Exception {
		World world = new World(new WorldConfiguration()
			.setSystem(TestSystemWithoutDelete.class)
			.setSystem(AnyOldeBaseSystem.class));

		Entity entity = world.createEntity();
		entity.edit().create(TestComponent.class);
		entity.deleteFromWorld();
		world.process();
		world.createEntity().edit().create(TestComponent.class);
		entity.deleteFromWorld();
		world.process();
		//This test is ok i 0.13.0
	}

	public static class TestSystemWithDelete extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;

		public TestSystemWithDelete() {
			super(Aspect.all(TestComponent.class));
		}

		@Override
		protected void process(Entity entity) {
			TestComponent testComponent = mapper.get(entity);
			assertNotNull("Entity with id <" + entity.getId() + "> has null component", testComponent);
			entity.deleteFromWorld();
		}
	}

	public static class TestSystemWithoutDelete extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;

		public TestSystemWithoutDelete() {
			super(Aspect.all(TestComponent.class));
		}

		@Override
		protected void process(Entity entity) {
			TestComponent testComponent = mapper.get(entity);
			assertNotNull("Entity with id <" + entity.getId() + "> has null component", testComponent);
		}
	}

	public static class AnyOldeBaseSystem extends BaseSystem {
		@Override
		protected void processSystem() {
		}
	}

	public static class TestComponent extends Component {
	}

}
