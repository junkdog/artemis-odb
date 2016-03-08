package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.artemis.systems.IteratingSystem;
import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WeirdStateTest {
	private static int lastId;

	@Test
	public void all_entities_contained_in_correct_state_test() {
		World w = new World(new WorldConfiguration()
			.setSystem(SysZero.class)
			.setSystem(SysA.class)
			.setSystem(SysB.class)
			.setSystem(SysC.class)
			.setSystem(SysD.class));

		w.process();
		w.process();
	}

	public static class SysZero extends BaseSystem {
		@Override
		protected void processSystem() {
			lastId = world.create();
			world.edit(lastId).create(ComponentX.class);
		}
	}

	public static class SysA extends IteratingSystem {
		public SysA() {
			super(all(ComponentX.class));
		}

		@Override
		protected void inserted(int entityId) {
			super.inserted(entityId);
		}

		@Override
		protected void process(int entityId) {
//			lastId = world.create();
		}
	}

	public static class SysB extends IteratingSystem {
		private EntityTransmuter transmuter;

		public SysB() {
			super(all(ComponentX.class));
		}

		@Override
		protected void initialize() {
			transmuter = new EntityTransmuterFactory(world)
				.add(ComponentY.class)
				.build();
		}

		@Override
		protected void process(int entityId) {
			transmuter.transmute(entityId);
			world.edit(lastId).create(ReusedComponent.class);
			world.delete(entityId);
		}
	}

	public static class SysC extends IteratingSystem {
		public SysC() {
			super(all(ComponentX.class));
		}

		@Override
		protected void process(int entityId) {

			int id = world.create();
			assertEquals(id, lastId);

			lastId = id;
		}
	}

	public static class SysD extends IteratingSystem {
		public SysD() {
			super(all(ComponentY.class));
		}

		@Override
		protected void inserted(int entityId) {
			super.inserted(entityId);
			fail("entity is dead");
		}

		@Override
		protected void process(int entityId) {
		}
	}

	public static class SysE extends IteratingSystem {
		public SysE() {
			super(all(ReusedComponent.class));
		}

		@Override
		protected void inserted(int entityId) {
			super.inserted(entityId);
			fail("entity is dead");
		}

		@Override
		protected void process(int entityId) {
		}
	}
}
