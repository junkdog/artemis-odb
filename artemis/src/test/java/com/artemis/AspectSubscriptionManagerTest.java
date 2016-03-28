package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import org.junit.Before;
import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.assertEquals;

public class AspectSubscriptionManagerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
	}

	private void entity(Class<? extends Component>... components) {
		EntityEdit ee = world.createEntity().edit();
		for (Class<? extends Component> c : components) {
			ee.create(c);
		}
	}

	@Test
	public void deleted_entities_not_in_new_subscriptions() {
		world = new World(new WorldConfiguration()
			.setSystem(new BaseSystem() {
				boolean hasRun;
				@Override
				protected void processSystem() {
					if (hasRun)
						return;

					IntBag original = world.getAspectSubscriptionManager()
						.get(all())
						.getEntities();

					assertEquals(1, original.size());

					for (int i = 0; i < original.size(); i++) {
						world.delete(original.get(i));
					}

					hasRun = true;
				}


			}));

		entity(ComponentX.class);
		world.process();

		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		IntBag empty = asm.get(all(ComponentX.class)).getEntities();
		assertEquals(0, empty.size());
	}

	@Test
	public void creating_subscriptions_at_any_time() {
		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		EntitySubscription subscription1 = asm.get(all(ComponentY.class));

		entity(ComponentX.class, ComponentY.class);

		world.process();

		EntitySubscription subscription2 = asm.get(all(ComponentX.class));

		assertEquals(1, subscription1.getEntities().size());
		assertEquals(1, subscription2.getEntities().size());
	}

	@Test
	public void entity_change_events_cleared() {
		world = new World(new WorldConfiguration().setSystem(new BootstrappingManager()));
		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		EntitySubscription sub = asm.get(all(ComponentX.class));
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

	@Test
	public void create_delete_same_tick() {
		CreateInremoveSystem es = new CreateInremoveSystem();
		World w = new World(new WorldConfiguration().setSystem(es));

		int original = w.create();
		w.edit(original).create(ComponentX.class);
		w.edit(original).create(ComponentY.class);

		int copy = w.create();
		w.edit(copy).create(ComponentX.class);
		w.edit(copy).create(ComponentY.class);

		w.process();

		assertEquals("entites should have same components/compositionId",
			w.getEntity(original).getCompositionId(),
			w.getEntity(es.replacedEntityId).getCompositionId());
	}

	public static class CreateInremoveSystem extends IteratingSystem {
		private ComponentMapper<ComponentX> componentXMapper;

		int replacedEntityId = -1;
		int deleteCount = 0;

		public CreateInremoveSystem() {
			super(all(ComponentX.class));
		}

		@Override
		protected void removed(int entityId) {
			int e = world.create();
			world.edit(e).create(ComponentX.class);
			world.edit(e).create(ComponentY.class);
			replacedEntityId = e;
		}

		@Override
		protected void process(int entityId) {
			if (entityId == 1 && deleteCount == 0) {
				world.delete(entityId);
				deleteCount++;
			}
		}
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
		public void added(Entity entityId) {
			if (!componentXMapper.has(entityId))
				return;

			entityId.edit().create(ComponentY.class);
		}
	}
}
