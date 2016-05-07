package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.utils.IntBag;
import com.google.gwt.junit.client.GWTTestCase;

import static com.artemis.Aspect.all;

public class EntitySubscriptionTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_aspect_builder_equality_test() {
		Aspect.Builder allX = all(ComponentX.class);
		Aspect.Builder allX2 = all(ComponentX.class);
		assertEquals(allX, allX2);
		assertEquals(allX.hashCode(), allX2.hashCode());
	}

	public void test_entity_subscriptions_are_reused_when_appropriate_test() {
		World world = new World();

		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		EntitySubscription subscription = asm.get(all(ComponentX.class));

		assertSame(subscription, asm.get(all(ComponentX.class)));
		assertNotSame(subscription, asm.get(all(ComponentX.class).exclude(ComponentY.class)));
	}

	public void test_deleted_entities_retain_components() {
		World world = new World();

		final ComponentMapper<ComponentY> mapper = world.getMapper(ComponentY.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentY.class))
			.addSubscriptionListener(new DeletedRetained(mapper));

		int id1 = world.create();
		world.edit(id1).create(ComponentY.class);

		world.process();
		world.delete(id1);
		world.process();

		int id2 = world.create();
		world.edit(id2).create(ComponentY.class);

		world.process();
		world.delete(id2);
		world.process();
	}

	public void test_removed_component_retained_in_remove() {
		World world = new World();

		final ComponentMapper<ComponentX> mapper = world.getMapper(ComponentX.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentX.class))
			.addSubscriptionListener(new RemovedRetainedInRemoveListener(mapper));

		int id1 = world.create();
		world.edit(id1).create(ComponentX.class);

		world.process();
		world.edit(id1).remove(ComponentX.class);
		world.process();

		world.edit(id1).create(ComponentX.class);

		world.process();
		world.edit(id1).remove(ComponentX.class);
		world.process();
	}

	public void test_removed_component_not_retained_in_remove() {
		World world = new World();

		final ComponentMapper<ComponentY> mapper = world.getMapper(ComponentY.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentY.class))
			.addSubscriptionListener(new RemovedNotReainted(mapper));

		int id1 = world.create();
		world.edit(id1).create(ComponentY.class);

		world.process();
		world.edit(id1).remove(ComponentY.class);
		world.process();

		world.edit(id1).create(ComponentY.class);

		world.process();
		world.edit(id1).remove(ComponentY.class);
		world.process();
	}

	public void test_manager_entity_subscription_test() {
		SubscribingManager sm = new SubscribingManager();
		World world = new World(new WorldConfiguration()
				.setSystem(sm));

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

		world.delete(0);
		world.process();

		assertEquals(3, sm.inserted);
		assertEquals(3, sm.removed);
	}

	public void test_subscription_remove_id_matches_entity() {
		WorldConfiguration config = new WorldConfiguration();
		config.setSystem(new TestManager());
		World world = new World(config);

		Entity entity = world.createEntity();
		world.process();
		entity.deleteFromWorld();
		world.process();
	}

	public static class SubscribingManager extends Manager
			implements  EntitySubscription.SubscriptionListener {

		int inserted, removed;

		@Override
		protected void initialize() {
			AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
			EntitySubscription subscription = asm.get(all(ComponentX.class));
			subscription.addSubscriptionListener(this);
		}

		public void killAlmostAll() {
			AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
			EntitySubscription subscription = asm.get(all(ComponentX.class));
			IntBag entities = subscription.getEntities();
			for (int i = 0, s = entities.size(); s > i; i++) {
				if (entities.get(i) > 0)
					world.delete(entities.get(i));
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

	public static class TestManager extends Manager {
			AspectSubscriptionManager subscriptionManager;

			@Override
			protected void initialize() {
				EntitySubscription subscription = subscriptionManager.get(all());

				subscription.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
					@Override
					public void inserted(IntBag entities) {
						int[] data = entities.getData();
						for (int i = 0; i < entities.size(); i++) {
							int entityId = data[i];
							assertEquals(0, entityId);

							assertNotNull(world.getEntity(entityId));
						}
					}

					@Override
					public void removed(IntBag entities) {
						int[] data = entities.getData();
						for (int i = 0; i < entities.size(); i++) {
							int entityId = data[i];
							assertEquals(0, entityId);
							assertNotNull(world.getEntity(entityId));
						}
					}
				});
			}
		}

	public static class RemovedRetainedInRemoveListener implements EntitySubscription.SubscriptionListener {
		private final ComponentMapper<ComponentX> mapper;

		public RemovedRetainedInRemoveListener(ComponentMapper<ComponentX> mapper) {
			this.mapper = mapper;
		}

		@Override
		public void inserted(IntBag entities) {
			assertEquals(1, entities.size());
		}

		@Override
		public void removed(IntBag entities) {
			assertEquals(1, entities.size());
			assertNotNull(mapper.get(entities.get(0)));
		}
	}

	public static class RemovedNotReainted implements EntitySubscription.SubscriptionListener {
		private final ComponentMapper<ComponentY> mapper;

		public RemovedNotReainted(ComponentMapper<ComponentY> mapper) {
			this.mapper = mapper;
		}

		@Override
		public void inserted(IntBag entities) {
			assertEquals(1, entities.size());
		}

		@Override
		public void removed(IntBag entities) {
			assertEquals(1, entities.size());
			assertNull(mapper.get(entities.get(0)));
		}
	}

	public static class DeletedRetained implements EntitySubscription.SubscriptionListener {
		private final ComponentMapper<ComponentY> mapper;

		public DeletedRetained(ComponentMapper<ComponentY> mapper) {
			this.mapper = mapper;
		}

		@Override
		public void inserted(IntBag entities) {
			assertEquals(1, entities.size());
		}

		@Override
		public void removed(IntBag entities) {
			assertEquals(1, entities.size());
			assertNotNull(mapper.get(entities.get(0)));
		}
	}
}