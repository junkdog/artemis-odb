package com.artemis;

import com.artemis.common.TestEntitySystemA;
import com.artemis.common.TestEntitySystemB;
import com.artemis.common.TestEntitySystemC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class WorldConfigurationBuilderSystemTest {

	public static final int BASE_SYSTEM_COUNT = 3;
	private WorldConfigurationBuilder builder;

	@Before
	public void setUp() {
		builder = new WorldConfigurationBuilder();
	}

	@Test(expected = WorldConfigurationException.class)
	public void should_refuse_duplicate_ystems() {
		builder.with(new TestEntitySystemA(), new TestEntitySystemB(), new TestEntitySystemA()).build();
	}

	@Test
	public void should_create_systems_in_order() {
		BaseSystem system1 = new TestEntitySystemA();
		BaseSystem system2 = new TestEntitySystemB();
		BaseSystem system3 = new TestEntitySystemC();

		World world = new World(new WorldConfigurationBuilder()
				.with(system1, system2)
				.with(system3).build());

		Assert.assertEquals(system1, world.getSystems().get(BASE_SYSTEM_COUNT +0));
		Assert.assertEquals(system2, world.getSystems().get(BASE_SYSTEM_COUNT +1));
		Assert.assertEquals(system3, world.getSystems().get(BASE_SYSTEM_COUNT +2));
	}

	@Test
	public void should_not_carry_over_old_systems_to_new_world() {
		WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
		World world1 = new World(builder.with(new TestEntitySystemA()).build());
		World world2 = new World(builder.build());
		Assert.assertEquals(BASE_SYSTEM_COUNT, world2.getSystems().size());
	}

	@Test
	public void should_support_multiple_plugins_with_same_system_dependencies() {
		class SharedDependencyPlugin implements ArtemisPlugin {
			@Override
			public void setup(WorldConfigurationBuilder b) {
				builder.dependsOn(TestEntitySystemA.class);
			}
		}
		class SharedDependencyPluginB extends SharedDependencyPlugin {}

		final World world = new World(builder.with(new SharedDependencyPlugin(), new SharedDependencyPluginB()).build());
		Assert.assertNotNull(world.getSystem(TestEntitySystemA.class));
	}

	@Test
	public void should_register_systems_by_priority() {
		BaseSystem system1 = new TestEntitySystemA();
		BaseSystem system2 = new TestEntitySystemB();

		final World world = new World(new WorldConfigurationBuilder()
				.with(WorldConfigurationBuilder.Priority.NORMAL, system1)
				.with(WorldConfigurationBuilder.Priority.HIGHEST, system2).build());

		Assert.assertEquals("Expected system to be loaded by priority.", system1, getLastLoadedSystem(world));
	}

	@Test
	public void should_register_dependency_systems_by_priority() {

		final World world = new World(new WorldConfigurationBuilder()
				.dependsOn(WorldConfigurationBuilder.Priority.NORMAL, TestEntitySystemA.class)
				.dependsOn(WorldConfigurationBuilder.Priority.HIGHEST, TestEntitySystemB.class).build());

		Assert.assertEquals("Expected system to be loaded by priority.", TestEntitySystemA.class, getLastLoadedSystem(world).getClass());
	}

	@Test
	public void should_preserve_system_order_within_same_priority() {

		final World world = new World(new WorldConfigurationBuilder()
				.dependsOn(WorldConfigurationBuilder.Priority.NORMAL, TestEntitySystemA.class, TestEntitySystemC.class)
				.dependsOn(WorldConfigurationBuilder.Priority.HIGHEST, TestEntitySystemB.class).build());

		Assert.assertEquals("Expected system to be loaded by priority.", TestEntitySystemC.class, getLastLoadedSystem(world).getClass());
	}

	private BaseSystem getLastLoadedSystem(World world) {
		return world.getSystems().get(world.getSystems().size()-1);
	}

}
