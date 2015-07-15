package com.artemis;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
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

	@Test
	public void aspect_exclude_only() {
		World w = new World();
		ExcludingSystem es1 = w.setSystem(new ExcludingSystem());
		EmptySystem es2 = w.setSystem(new EmptySystem());
		w.initialize();

		Entity e = w.createEntity();
		w.process();

		System.out.printf("%s=%d\n", e, e.getCompositionId());

		assertEquals(1, es1.getActives().size());
		assertEquals(1, es2.getActives().size());
	}

	public static class C extends Component {}
	public static class C2 extends Component {}

	public static class IteratorTestSystem extends EntitySystem {
		public int expectedSize;
		
		@SuppressWarnings("unchecked")
		public IteratorTestSystem(int expectedSize) {
			super(Aspect.all(C.class));
			this.expectedSize = expectedSize;
		}

		@Override
		protected void processSystem() {
			assertEquals(expectedSize, subscription.getEntities().size());
			getActives().iterator().next();
		}

		@Override
		protected boolean checkProcessing() {
			return true;
		}
	}

	public static class ExcludingSystem extends EntityProcessingSystem {
		public ExcludingSystem() {
			super(Aspect.exclude(C.class));
		}

		@Override
		protected void process(Entity e) {}
	}

	public static class EmptySystem extends EntityProcessingSystem {
		public EmptySystem() {
			super(Aspect.all());
		}

		@Override
		protected void process(Entity e) {}
	}
}
