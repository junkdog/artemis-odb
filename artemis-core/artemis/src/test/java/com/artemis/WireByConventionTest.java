package com.artemis;

import com.artemis.annotations.Wire;
import org.junit.Assert;
import org.junit.Test;

/**
 * Wire applies by convention.
 *
 * @author Daan van Yperen
 */
public class WireByConventionTest {

	@Test
	public void ensure_wire_implicit_by_convention() {
		class TestSystem extends BaseSystem {

			@Override
			protected void processSystem() {

			}
		}
		class TestSystem2 extends BaseSystem {

			TestSystem x;
			@Override
			protected void processSystem() {

			}
		}

		TestSystem2 system2 = new TestSystem2();
		new World(new WorldConfiguration().setSystem(new TestSystem()).setSystem(system2));

		Assert.assertNotNull(system2.x);
	}

	@Test
	public void ensure_primitives_dont_cause_wire_exceptions() {

		class PrimitivesSystem extends BaseSystem {

			byte b;
			short s;
			int i;
			long l;
			float f;
			double d;
			boolean b2;
			char c;
			String z;

			@Override
			protected void processSystem() {
			}
		}

		new World(new WorldConfiguration().setSystem(new PrimitivesSystem()));
	}

	@Test
	public void ensure_explicit_wire_overrides_implicit_wire() {

		class TestSystem extends BaseSystem {
			@Override
			protected void processSystem() {
			}
		}

		class SuperSystem extends BaseSystem {

			TestSystem x;

			@Override
			protected void processSystem() {
			}
		}

		@Wire(injectInherited = false)
		class ExplicitlyWired extends SuperSystem {

			TestSystem y;

			@Override
			protected void processSystem() {
			}
		}

		ExplicitlyWired explicitlyWired = new ExplicitlyWired();
		new World(new WorldConfiguration()
				.setSystem(explicitlyWired)
				.setSystem(new TestSystem()));

		Assert.assertNotNull(explicitlyWired.y);
		Assert.assertNull(explicitlyWired.x);
	}

	@Test
	public void When_implicit_wire_Then_inject_into_superclasses() {

		class TestSystem extends BaseSystem {
			@Override
			protected void processSystem() {
			}
		}

		class SuperSystem extends BaseSystem {

			TestSystem x;

			@Override
			protected void processSystem() {
			}
		}

		class ImplicitlyWired extends SuperSystem {

			TestSystem y;

			@Override
			protected void processSystem() {
			}
		}

		ImplicitlyWired implicitlyWired = new ImplicitlyWired();
		new World(new WorldConfiguration()
				.setSystem(implicitlyWired)
				.setSystem(new TestSystem()));

		Assert.assertNotNull(implicitlyWired.y);
		Assert.assertNotNull(implicitlyWired.x);
	}
}
