package com.artemis;

import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
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
		EntityHelper.edit(world, world.createEntity()).create(TestComponent.class);
		world.process();

		EntityHelper.edit(world, world.createEntity()).create(TestComponent.class);
		world.process(); //This test fails in 0.13.0!

	}

	@Test
	public void test_one_system_in_world_delete_during_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithDelete.class));
		EntityHelper.edit(world, world.createEntity()).create(TestComponent.class);
		world.process();

		EntityHelper.edit(world, world.createEntity()).create(TestComponent.class);
		world.process();
		//This test is ok i 0.13.0
	}

	@Test
	public void test_two_systems_in_world_delete_after_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithoutDelete.class)
				.setSystem(AnyOldeBaseSystem.class));
		int entity = world.createEntity();
		EntityHelper.edit(world, entity).create(TestComponent.class);
		world.process();
		world.deleteEntity(entity);

		int entity2 = world.createEntity();
		EntityHelper.edit(world, entity2).create(TestComponent.class);
		world.process();
		world.deleteEntity(entity2);
		world.process();
		//This test is ok i 0.13.0
	}

	@Test
	public void test_two_systems_in_world_delete_before_process() throws Exception {
		World world = new World(new WorldConfiguration().setSystem(TestSystemWithoutDelete.class)
				.setSystem(AnyOldeBaseSystem.class));
		int entity = world.createEntity();
		EntityHelper.edit(world, entity).create(TestComponent.class);
		world.deleteEntity(entity);
		world.process();
		EntityHelper.edit(world, world.createEntity()).create(TestComponent.class);
		world.deleteEntity(entity);
		world.process();
		//This test is ok i 0.13.0
	}

	public static class TestSystemWithDelete extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;

		public TestSystemWithDelete() {
			super(Aspect.all(TestComponent.class));
		}

		@Override
		public void inserted(IntBag entities) {
			super.inserted(entities);
		}

		@Override
		protected void process(int entity) {
			TestComponent testComponent = mapper.get(entity);
			assertNotNull("int with id <" + entity + "> has null component", testComponent);
			world.deleteEntity(entity);
		}
	}

	public static class TestSystemWithoutDelete extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;

		public TestSystemWithoutDelete() {
			super(Aspect.all(TestComponent.class));
		}

		@Override
		protected void process(int entity) {
			TestComponent testComponent = mapper.get(entity);
			assertNotNull("int with id <" + entity + "> has null component", testComponent);
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
