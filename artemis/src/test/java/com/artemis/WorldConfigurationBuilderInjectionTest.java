package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.injection.FieldResolver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Daan van Yperen
 */
public class WorldConfigurationBuilderInjectionTest {

	private WorldConfigurationBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new WorldConfigurationBuilder();
	}

	@Test
	public void custom_field_resolvers_should_not_suppress_default_wiring() {

		FieldResolver resolver = mock(FieldResolver.class);
		CoopSystem coopSystem = new CoopSystem();
		builder
				.with(coopSystem)
				.register(resolver);

		WorldConfiguration configuration = builder.build();
		Chicken chicken = new Chicken();
		configuration.register(chicken);

		new World(configuration);
		assertEquals(chicken,coopSystem.myChicken);
		assertNotNull(coopSystem.mFlaming);
	}

	public static class Chicken {};
	public static class Flaming extends Component {};
	public static class CoopSystem extends BaseSystem {

		@Wire
		public Chicken myChicken;
		public ComponentMapper<Flaming> mFlaming;

		@Override
		protected void processSystem() {
		}
	}
}
