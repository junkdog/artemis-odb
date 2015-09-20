package com.artemis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.artemis.systems.EntityProcessingSystem;

@SuppressWarnings("static-method")
public class Issue206SystemTest {

	@Test
	public void test_edited_bitset_sanity() {
		World world = new World(new WorldConfiguration()
				.setSystem(new TestSystemAB()));

		int e = world.createEntity();
		EntityHelper.edit(world, e).create(CompA.class);
		EntityHelper.edit(world, e).create(CompB.class);
		EntityHelper.edit(world, e).create(TestComponentC.class);

		world.process();
		
		assertSame(EntityHelper.edit(world, e), EntityHelper.edit(world, e));
		EntityHelper.edit(world, e).remove(CompB.class);
		// nota bene: in 0.7.0 and 0.7.1, chaining edit() caused
		// the componentBits to reset
		EntityHelper.edit(world, e).remove(TestComponentC.class);

		world.process();
		world.process();
	}

	public static class CompA extends Component {}
	public static class CompB extends Component {}
	public static class TestComponentC extends Component {}

	private static class TestSystemAB extends EntityProcessingSystem {
		@SuppressWarnings("unchecked")
		public TestSystemAB() {
			super(Aspect.all(CompA.class, CompB.class));
		}

		@Override
		protected void process(int e) {
			assertNotNull(EntityHelper.getComponent(CompB.class, world, e));
		}
	}
}
