package com.artemis;

import com.artemis.systems.EntityProcessingSystem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * PerformerWorldTest.
 * JUnit.
 * @author lopho
 */
public class PerformerWorldTest {

	public PerformerWorldTest() {
	}

	private World world;
	private int step;

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		step = 0;

		world = new World();
		world.setSystem(new SystemA());
		//world.setSystem(new SystemB());
		world.initialize();

		for (int i = 0; i < 10; i++) {
			world.createEntity().addComponent(new TestComponent(i)).addToWorld();
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





/////// System and Component classes
	private static class SystemA extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;
		public int step;

		public SystemA() {
			super(Aspect.getAspectForOne(TestComponent.class));
		}

		@Override
		protected void initialize() {
			step = 0;
			mapper = world.getMapper(TestComponent.class);
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

	private static class SystemB extends EntityProcessingSystem {
		private ComponentMapper<TestComponent> mapper;
		public int step;

		public SystemB() {
			super(Aspect.getAspectForOne(TestComponent.class));
		}

		@Override
		protected void initialize() {
			step = 0;
			mapper = world.getMapper(TestComponent.class);
		}

		@Override
		protected void process(Entity e) {
			mapper.getSafe(e).getValue();
		}
	}

	private static class TestComponent extends Component {
		public final int value;

		public TestComponent(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
