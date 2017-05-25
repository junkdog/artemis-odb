package com.artemis;

import com.artemis.injection.CachedInjector;
import com.artemis.injection.Injector;
import com.artemis.utils.ImmutableBag;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class WorldConfigurationTest {

	@Test
	public void should_use_default_invocation_strategy_when_none_specified() {
		World world = new World(new WorldConfiguration());

		Assert.assertTrue(world.getInvocationStrategy() instanceof InvocationStrategy);
	}

	@Test
	public void should_use_override_invocation_strategy_when_specified() {
		SystemInvocationStrategy strategy = new SystemInvocationStrategy() {
			@Override
			protected void process() {
			}
		};
		World world = new World(new WorldConfiguration().setInvocationStrategy(strategy));
		Assert.assertTrue(world.getInvocationStrategy() == strategy);
	}

	@Test(expected = NullPointerException.class)
	public void should_npe_null_entity_invocation_strategy_immediately() {
		new World(new WorldConfiguration().setInvocationStrategy(null));
	}


	@Test
	public void should_use_default_injector_when_none_specified() {
		World world = new World(new WorldConfiguration());

		Assert.assertTrue(world.getInjector() instanceof CachedInjector);
	}

	@Test
	public void should_use_override_injector_when_specified() {
		Injector myInjector = new CachedInjector();
		World world = new World(new WorldConfiguration().setInjector(myInjector));
		Assert.assertTrue(world.getInjector() == myInjector);
	}

	@Test(expected = NullPointerException.class)
	public void should_npe_null_injector_immediately() {
		new World(new WorldConfiguration().setInjector(null));
	}

	@Test
	public void should_not_contain_null_systems_in_invocation_strategy_ref_issue_383() {
		SystemInvocationStrategy strategy = new SystemInvocationStrategy() {
			@Override
			protected void initialize() {
				ImmutableBag<BaseSystem> systems = world.getSystems();
				for (int i = 0; i < systems.size(); i++) {
					Assert.assertNotNull(systems.get(i));
				}
			}

			@Override
			protected void process() {
			}
		};
		World world = new World(new WorldConfiguration().setInvocationStrategy(strategy));
		Assert.assertTrue(world.getInvocationStrategy() == strategy);
	}

}