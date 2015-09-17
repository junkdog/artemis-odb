package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Daan van Yperen
 */
public class SkipWireTest {

	@Test
	public void ensure_skipped_field_skipped_in_wired_class() {

		class TestManager extends Manager {
			private ComponentMapper<ComponentX> x;
			@SkipWire
			private ComponentMapper<ComponentY> y;
		}


		TestManager manager = new TestManager();
		new World(new WorldConfiguration()
				.setSystem(manager));

		assertNotNull(manager.x);
		assertNull(manager.y);
	}

	@Test
	public void ensure_superclass_skipped_field_skipped_in_wired_class() {

		class SuperTestManager extends Manager {
			protected ComponentMapper<ComponentX> x;
			@SkipWire
			protected ComponentMapper<ComponentY> y;
		}

		class TestManager extends SuperTestManager {
		}


		TestManager manager = new TestManager();
		new World(new WorldConfiguration()
				.setSystem(manager));

		assertNotNull(manager.x);
		assertNull(manager.y);
	}


	@Test
	public void ensure_skipped_superclass_skipped_in_wired_class() {

		class RootTestManager extends Manager {
			protected ComponentMapper<ComponentY> z;
		}

		@SkipWire
		class SuperTestManager extends RootTestManager {
			protected ComponentMapper<ComponentY> y;
		}

		class TestManager extends SuperTestManager {
			protected ComponentMapper<ComponentX> x;
		}


		TestManager manager = new TestManager();
		new World(new WorldConfiguration()
				.setSystem(manager));

		assertNotNull(manager.z);
		assertNull(manager.y);
		assertNotNull(manager.x);
	}

	@Test
	public void ensure_wire_supersedes_skipwire_on_type() {

		@Wire @SkipWire
		class TestManager extends Manager {
			private ComponentMapper<ComponentX> x;
		}


		TestManager manager = new TestManager();
		new World(new WorldConfiguration()
				.setSystem(manager));

		assertNotNull(manager.x);
	}

	@Test
	public void ensure_explicit_wire_supersedes_skipwire_on_field() {


		class TestManager extends Manager {
			@Wire @SkipWire
			private ComponentMapper<ComponentX> x;
		}


		TestManager manager = new TestManager();
		new World(new WorldConfiguration()
				.setSystem(manager));

		assertNotNull(manager.x);
	}

}
