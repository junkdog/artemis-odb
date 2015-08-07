package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntitySubscriptionTest {

	@Test
	public void aspect_builder_equality_test() {
		Aspect.Builder allX = Aspect.all(ComponentX.class);
		Aspect.Builder allX2 = Aspect.all(ComponentX.class);
		assertEquals(allX, allX2);
		assertEquals(allX.hashCode(), allX2.hashCode());
	}

	@Test
	public void entity_subscriptions_are_reused_when_appropriate_test() {
		World world = new World();

		AspectSubscriptionManager asm = world.getManager(AspectSubscriptionManager.class);
		EntitySubscription subscription = asm.get(Aspect.all(ComponentX.class));

		assertSame(subscription, asm.get(Aspect.all(ComponentX.class)));
		assertNotSame(subscription, asm.get(Aspect.all(ComponentX.class).exclude(ComponentY.class)));
	}

	@Test
	public void manager_entity_subscription_test() {
		SubscribingManager sm = new SubscribingManager();
		World world = new World(new WorldConfiguration()
				.setManager(sm));

		assertEquals(0, sm.inserted);
		assertEquals(0, sm.removed);

		world.createEntity().edit().create(ComponentX.class);
		world.createEntity().edit().create(ComponentX.class);
		world.createEntity().edit().create(ComponentX.class);
		world.process();

		assertEquals(3, sm.inserted);
		assertEquals(0, sm.removed);

		sm.killAlmostAll();
		world.process();

		assertEquals(3, sm.inserted);
		assertEquals(2, sm.removed);

		world.deleteEntity(0);
		world.process();

		assertEquals(3, sm.inserted);
		assertEquals(3, sm.removed);
	}


	private static class SubscribingManager extends Manager
			implements  EntitySubscription.SubscriptionListener {

		int inserted, removed;

		@Override
		protected void initialize() {
			AspectSubscriptionManager asm = world.getManager(AspectSubscriptionManager.class);
			EntitySubscription subscription = asm.get(Aspect.all(ComponentX.class));
			subscription.addSubscriptionListener(this);
		}

		public void killAlmostAll() {
			AspectSubscriptionManager asm = world.getManager(AspectSubscriptionManager.class);
			EntitySubscription subscription = asm.get(Aspect.all(ComponentX.class));
			IntBag entities = subscription.getEntities();
			for (int i = 0, s = entities.size(); s > i; i++) {
				if (entities.get(i) > 0)
					world.deleteEntity(entities.get(i));
			}
		}

		@Override
		public void inserted(IntBag entities) {
			inserted += entities.size();
		}

		@Override
		public void removed(IntBag entities) {
			removed += entities.size();
		}
	}
}