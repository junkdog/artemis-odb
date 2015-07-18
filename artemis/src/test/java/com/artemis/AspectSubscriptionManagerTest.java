package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AspectSubscriptionManagerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
	}

	private void entity(Class<? extends Component>... components) {
		EntityEdit ee = world.createEntity().edit();
		for (Class<? extends Component> c : components) {
			ee.create(c);
		}
	}

	@Test
	public void creating_subscriptions_at_any_time() {
		AspectSubscriptionManager asm = world.getManager(AspectSubscriptionManager.class);
		EntitySubscription subscription1 = asm.get(Aspect.all(ComponentY.class));

		entity(ComponentX.class, ComponentY.class);

		world.process();

		EntitySubscription subscription2 = asm.get(Aspect.all(ComponentX.class));

		assertEquals(1, subscription1.getEntities().size());
		assertEquals(1, subscription2.getEntities().size());
	}
}
