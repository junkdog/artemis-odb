package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
/**
 * @author Daan van Yperen
 */
public class ComponentMapperTest {

	public static class Pos extends Component { public int x,y; public Pos() {} public Pos(int x, int y){ this.x=x;this.y=y; } }
	public static class TestMarker extends Component {}

	@Wire
	public class BasicSystem extends EntityProcessingSystem {

		public BasicSystem() {
			super(Aspect.all(TestMarker.class));
		}

		protected ComponentMapper<Pos> mPos;

		@Override
		protected void process(int e) {
		}
	}

	@Test
	public void create_if_missing_should_create_new_component() {

		@Wire(injectInherited = true)
		class TestSystemA extends BasicSystem {
			@Override
			protected void process(int e) {
				Pos c = mPos.create(e);
				Assert.assertNotNull(c);
			}
		}

		createAndProcessWorld(new TestSystemA());
	}

	@Test
	public void create_if_exists_should_recycle_existing_component() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				Pos c1 = mPos.create(e);
				Pos c2 = mPos.create(e);
				Assert.assertEquals(c1, c2);
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void set_if_exists_should_recycle_existing_component() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				Pos c1 = mPos.create(e);
				Pos c2 = mPos.set(e, true);
				Assert.assertEquals(c1, c2);
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void set_if_exists_should_remove_component() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				mPos.create(e);
				mPos.set(e,false);
				Assert.assertFalse(mPos.has(e));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void remove_if_exists_should_remove_component() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				mPos.create(e);
				mPos.remove(e);
				Assert.assertFalse(mPos.has(e));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void remove_if_missing_should_not_explode() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				mPos.remove(e);
				Assert.assertFalse(mPos.has(e));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void set_if_missing_should_not_explode() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				mPos.set(e, false);
				Assert.assertFalse(mPos.has(e));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void getsafe_with_fallback_should_return_fallback_when_component_missing() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {

			private Pos fallbackPos = new Pos(10,10);

			@Override
			protected void process(int e) {
				Assert.assertEquals("expected to return fallback.", fallbackPos, mPos.getSafe(e, fallbackPos));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void getsafe_with_fallback_should_return_component_when_available() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {

			private Pos fallbackPos = new Pos(10,10);

			@Override
			protected void process(int e) {
				final Pos pos = mPos.create(e);
				Assert.assertEquals("expected to return component.", pos, mPos.getSafe(e, fallbackPos));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void getsafe_with_null_fallback_should_return_null() {

		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {

			private Pos fallbackPos = new Pos(10,10);

			@Override
			protected void process(int e) {
				Assert.assertNull(mPos.getSafe(e, null));
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	protected void createAndProcessWorld(BaseSystem system) {
		final World world = new World(new WorldConfiguration().setSystem(system));
		EntityHelper.edit(world, world.createEntity()).create(TestMarker.class);
		world.process();
	}

	@Test
	public void create_right_after_entity_creation_should_not_throw_exception() {
		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				final int t1 = world.createEntity();
				Pos c1 = mPos.create(t1);
				Assert.assertNotNull(c1);
			}
		}
		createAndProcessWorld(new TestSystem());
	}


	@Test
	public void remove_right_after_entity_creation_should_not_throw_exception() {
		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				final int t1 = world.createEntity();
				mPos.remove(t1);
			}
		}
		createAndProcessWorld(new TestSystem());
	}

	@Test
	public void create_by_id_right_after_entity_creation_should_not_throw_exception() {
		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				final int t1 = world.createEntity();
				Pos c1 = mPos.create(t1);
				Assert.assertNotNull(c1);
			}
		}
		createAndProcessWorld(new TestSystem());
	}


	@Test
	public void remove_by_id_right_after_entity_creation_should_not_throw_exception() {
		@Wire(injectInherited = true)
		class TestSystem extends BasicSystem {
			@Override
			protected void process(int e) {
				final int t1 = world.createEntity();
				mPos.remove(t1);
			}
		}
		createAndProcessWorld(new TestSystem());
	}


}