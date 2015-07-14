package com.artemis;


import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubscriptionListenerTest {

	@Test
	public void subscriptionlistener_inform_about_first_entities_test() {
		World world = new World();
		Es1 es1 = world.setSystem(new Es1());
		Es2 es2 = world.setSystem(new Es2());
		world.initialize();

		world.process();

		assertTrue(es2.hasInserted);

		world.process();
	}

	public static class MyComponent extends Component {
	}

	private static class Es1 extends BaseSystem {
		private boolean isInitialized;

		@Override
		protected void processSystem() {
			if (!isInitialized) {
				world.createEntity().edit().create(MyComponent.class);
				isInitialized = true;
			}
		}
	}

	@Wire
	private static class Es2 extends BaseSystem implements SubscriptionListener {
		private ComponentMapper<MyComponent> mapper;
		private boolean hasInserted;

		@Override
		protected void processSystem() {
			assertTrue(hasInserted);
		}

		@Override
		protected void initialize() {
			AspectSubscriptionManager am = world.getManager(AspectSubscriptionManager.class);
			am.get(Aspect.all(MyComponent.class)).addSubscriptionListener(this);
		}

		@Override
		public void inserted(ImmutableBag<Entity> entities) {
			for (Entity e : entities) {
				assertNotNull(mapper.get(e));
				hasInserted = true;
			}
		}

		@Override
		public void removed(ImmutableBag<Entity> entities) {

		}
	}
}
