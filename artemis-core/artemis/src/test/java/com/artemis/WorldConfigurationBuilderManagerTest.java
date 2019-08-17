package com.artemis;

import com.artemis.common.TestEntityManagerA;
import com.artemis.common.TestEntityManagerB;
import com.artemis.common.TestEntityManagerC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;import java.lang.Exception;import java.lang.Override;

/**
 * @author Daan van Yperen
 */
public class WorldConfigurationBuilderManagerTest {

	private WorldConfigurationBuilder builder;

	@Before
	public void setUp() {
		builder = new WorldConfigurationBuilder();
	}

	@Test(expected = WorldConfigurationException.class)
	public void should_refuse_duplicate_managers() {
		builder.with(new TestEntityManagerA(), new TestEntityManagerB(), new TestEntityManagerA()).build();
	}

	@Test
	public void should_support_multiple_plugins_with_same_manager_dependencies() {
		class SharedDependencyPlugin implements ArtemisPlugin {
			@Override
			public void setup(WorldConfigurationBuilder b) {
				builder.dependsOn(TestEntityManagerA.class);
			}
		}
		class SharedDependencyPluginB extends SharedDependencyPlugin {}

		final World world = new World(builder.with(new SharedDependencyPlugin(), new SharedDependencyPluginB()).build());
		Assert.assertNotNull(world.getSystem(TestEntityManagerA.class));
	}

	@Test
	public void should_register_managers_by_priority() {
		Manager manager1 = new TestEntityManagerA();
		Manager manager2 = new TestEntityManagerB();

		final World world = new World(new WorldConfigurationBuilder()
				.with(WorldConfigurationBuilder.Priority.NORMAL, manager1)
				.with(WorldConfigurationBuilder.Priority.HIGHEST, manager2).build());

		Assert.assertEquals("Expected manager to be loaded by priority.", manager1, getLastLoadedManager(world));
	}

	@Test
	public void should_register_dependency_managers_by_priority() {

		final World world = new World(new WorldConfigurationBuilder()
				.dependsOn(WorldConfigurationBuilder.Priority.NORMAL, TestEntityManagerA.class)
				.dependsOn(WorldConfigurationBuilder.Priority.HIGHEST, TestEntityManagerB.class).build());

		Assert.assertEquals("Expected manager to be loaded by priority.", TestEntityManagerA.class, getLastLoadedManager(world).getClass());
	}

	@Test
	public void should_preserve_order_registering_managers_with_same_priority() {

		final World world = new World(new WorldConfigurationBuilder()
				.dependsOn(WorldConfigurationBuilder.Priority.NORMAL, TestEntityManagerA.class, TestEntityManagerC.class)
				.dependsOn(WorldConfigurationBuilder.Priority.HIGHEST, TestEntityManagerB.class).build());

		Assert.assertEquals("Expected manager to be loaded by priority.", TestEntityManagerC.class, getLastLoadedManager(world).getClass());
	}

	protected BaseSystem getLastLoadedManager(World world) {
		return world.getSystems().get(world.getSystems().size()-1);
	}


	@Test
	public void should_create_managers_on_build() {
		Manager manager1 = new TestEntityManagerA();
		Manager manager2 = new TestEntityManagerB();

		WorldConfiguration config = new WorldConfigurationBuilder()
				.with(manager1, manager2).build();

		World world = new World(config);

		Assert.assertTrue(world.getSystems().contains(manager1));
		Assert.assertTrue(world.getSystems().contains(manager2));
	}

}
