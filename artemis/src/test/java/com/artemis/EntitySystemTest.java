package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.artemis.utils.ImmutableBag;

/**
 * Created by obartley on 6/9/14.
 */
public class EntitySystemTest {

	@SuppressWarnings("static-method")
	@Test(expected = NoSuchElementException.class)
	public void test_process_one_inactive() {
		World w = new World();

		w.setSystem(new IteratorTestSystem(0));
		w.initialize();

		Entity e = w.createEntity();
		e.edit().add(new C());
		e.disable();

		w.process();
	}

	@SuppressWarnings("static-method")
	@Test
	public void test_process_one_active() {
		World w = new World();

		w.setSystem(new IteratorTestSystem(1));
		w.initialize();

		Entity e = w.createEntity();
		e.edit().add(new C());

		w.process();
	}

	public static class C extends Component {}

	public static class IteratorTestSystem extends EntitySystem {
		public int expectedSize;
		
		@SuppressWarnings("unchecked")
		public IteratorTestSystem(int expectedSize) {
			super(Aspect.getAspectForAll(C.class));
			this.expectedSize = expectedSize;
		}

		@Override
		protected void processEntities(ImmutableBag<Entity> entities) {
			assertEquals(expectedSize, entities.size());
			entities.iterator().next();
		}

		@Override
		protected boolean checkProcessing() {
			return true;
		}
	}
}
