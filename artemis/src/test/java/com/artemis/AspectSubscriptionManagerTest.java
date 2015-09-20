package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.utils.IntBag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AspectSubscriptionManagerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
	}

	private void entity(Class<? extends Component>... components) {
		EntityEdit ee = EntityHelper.edit(world, world.createEntity());
		for (Class<? extends Component> c : components) {
			ee.create(c);
		}
	}

	@Test
	public void creating_subscriptions_at_any_time() {
		AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
		EntitySubscription subscription1 = asm.get(Aspect.all(ComponentY.class));

		entity(ComponentX.class, ComponentY.class);

		world.process();

		EntitySubscription subscription2 = asm.get(Aspect.all(ComponentX.class));

		assertEquals(1, subscription1.getEntities().size());
		assertEquals(1, subscription2.getEntities().size());
	}

	@Test
	public void entity_change_events_cleared() {
		world = new World(new WorldConfiguration().setSystem(new BootstrappingManager()));
		AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
		EntitySubscription sub = asm.get(Aspect.all(ComponentX.class));
		SubListener listener = new SubListener();
		sub.addSubscriptionListener(listener);

		entity(ComponentX.class);

		world.process();

		assertEquals(1, listener.totalInserted);
		entity(ComponentX.class);
		entity(ComponentX.class);

		world.process();
		assertEquals(3, listener.totalInserted);

		world.process();

		world.process();
		assertEquals(3, listener.totalInserted);
		world.process();
		assertEquals(3, listener.totalInserted);
	}

	private static class SubListener implements EntitySubscription.SubscriptionListener {

		private int totalInserted;

		@Override
		public void inserted(IntBag entities) {
			totalInserted += entities.size();
		}

		@Override
		public void removed(IntBag entities) {}
	}

	private static class BootstrappingManager extends Manager {
		private ComponentMapper<ComponentX> componentXMapper;

		@Override
		public void added(int entityId) {
			if (!componentXMapper.has(entityId))
				return;

			EntityHelper.edit(world, entityId).create(ComponentY.class);
		}
	}
}
