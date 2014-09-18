package com.artemis;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.artemis.systems.EntityProcessingSystem;

/**
 * PerformerWorldTest.
 * JUnit.
 * @author lopho
 */
public class PerformerWorldTest {

	public PerformerWorldTest() {
	}

	private World world;

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		world = new World();
		world.setSystem(new SystemA());
		//world.setSystem(new SystemB());
		world.initialize();

		for (int i = 0; i < 10; i++) {
			world.createEntity().edit().add(new TestComponent());
		}
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test_entity_removal_during_process() {
		world.process();
		world.process();
		world.process();
	}


	private static class SystemA extends EntityProcessingSystem {
		public int step;

		@SuppressWarnings("unchecked")
		public SystemA() {
			super(Aspect.getAspectForOne(TestComponent.class));
		}

		@Override
		protected void initialize() {
			step = 0;
		}

		@Override
		protected void process(Entity e) {
			try {
				e.deleteFromWorld();
			} catch (NullPointerException ex) {
				throw new NullPointerException(""+step);
			}
			step++;
		}
	}

	private static class TestComponent extends Component {
		public TestComponent() {}
	}
}
