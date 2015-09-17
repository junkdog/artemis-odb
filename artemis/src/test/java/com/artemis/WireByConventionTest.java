package com.artemis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class WireByConventionTest {

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
		Assert.fail();
	}
}
