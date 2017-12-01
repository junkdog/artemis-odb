package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;

import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.*;

public class EntitySubscriptionTest {

	@Test
	public void aspect_builder_equality_test() {
		Aspect.Builder allX = all(ComponentX.class);
		Aspect.Builder allX2 = all(ComponentX.class);
		assertEquals(allX, allX2);
		assertEquals(allX.hashCode(), allX2.hashCode());
	}

	@Test
	public void entity_subscriptions_are_reused_when_appropriate_test() {
		EntityWorld world = new EntityWorld();

		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		EntitySubscription subscription = asm.get(all(ComponentX.class));

		assertSame(subscription, asm.get(all(ComponentX.class)));
		assertNotSame(subscription, asm.get(all(ComponentX.class).exclude(ComponentY.class)));
	}

	@Test
	public void deleted_entities_retain_components() {
		EntityWorld world = new EntityWorld();

		final ComponentMapper<ComponentY> mapper = world.getMapper(ComponentY.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentY.class))
			.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
				@Override
				public void inserted(IntBag entities) {
					assertEquals(1, entities.size());
				}

				@Override
				public void removed(IntBag entities) {
					assertEquals(1, entities.size());
					assertNotNull(mapper.get(entities.get(0)));
				}
			});

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

	@Test
	public void removed_component_retained_in_remove() {
		EntityWorld world = new EntityWorld();

		final ComponentMapper<ComponentX> mapper = world.getMapper(ComponentX.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentX.class))
			.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
				@Override
				public void inserted(IntBag entities) {
					assertEquals(1, entities.size());
				}

				@Override
				public void removed(IntBag entities) {
					assertEquals(1, entities.size());
					assertNotNull(mapper.get(entities.get(0)));
				}
			});

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

	@Test
	public void removed_and_create_cancels_removed() {
		final EntityWorld world = new EntityWorld(new WorldConfiguration()
			.setSystem(new IteratingSystem(all(ComponentX.class)) {
				private ComponentMapper<ComponentX> xMapper;

				@Override
				protected void process(int entityId) {
					xMapper.remove(entityId);
				}
			}));

		final ComponentMapper<ComponentX> mapper = world.getMapper(ComponentX.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentY.class).exclude(ComponentX.class))
			.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
				@Override
				public void inserted(IntBag entities) {
					assertEquals(1, entities.size());
					world.edit(entities.get(0)).create(ComponentX.class);
				}

				@Override
				public void removed(IntBag entities) {
					assertEquals(1, entities.size());
				}
			});

		int id1 = world.create();
		world.edit(id1).create(ComponentX.class);
		world.edit(id1).create(ComponentY.class);

		world.process();
		assertNotNull(mapper.get(id1));
	}

	@Test
	public void removed_component_not_retained_in_remove() {
		EntityWorld world = new EntityWorld();

		final ComponentMapper<ComponentY> mapper = world.getMapper(ComponentY.class);

		world.getAspectSubscriptionManager()
			.get(all(ComponentY.class))
			.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
				@Override
				public void inserted(IntBag entities) {
					assertEquals(1, entities.size());
				}

				@Override
				public void removed(IntBag entities) {
					assertEquals(1, entities.size());
					assertNull(mapper.get(entities.get(0)));
				}
			});

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

	@Test
	public void manager_entity_subscription_test() {
		SubscribingManager sm = new SubscribingManager();
		EntityWorld world = new EntityWorld(new WorldConfiguration()
				.setSystem(sm));

		assertEquals(0, sm.inserted);
		assertEquals(0, sm.removed);

		world.edit(world.create()).create(ComponentX.class);
		world.edit(world.create()).create(ComponentX.class);
		world.edit(world.create()).create(ComponentX.class);
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

	@Test
	public void subscription_remove_id_matches_entity() {
		WorldConfiguration config = new WorldConfiguration();
		config.setSystem(new TestManager());
		EntityWorld world = new EntityWorld(config);

		Entity entity = world.createEntity();
		world.process();
		entity.deleteFromWorld();
		world.process();
	}
	
	@Test
	public void ensure_entity_subscription_integrity() {
		CompXCounterSystem counterX = new CompXCounterSystem();
		
		WorldConfiguration config = new WorldConfiguration();
		config.setSystem(counterX);
		
		EntityWorld world = new EntityWorld(config);

		ComponentMapper<ComponentX> mapperX = world.getMapper(ComponentX.class);
		ComponentMapper<ComponentY> mapperY = world.getMapper(ComponentY.class);
		
		assertEquals(0, counterX.insertedEntities);
		
		Entity entityX = world.createEntity();
		mapperX.create(entityX);
		
		world.process();
		assertEquals(1, counterX.insertedEntities);
		
		Entity entityY = world.createEntity();
		mapperY.create(entityY);

		world.process();
		assertEquals(1, counterX.insertedEntities);
		
		Entity entityXY = world.createEntity();
		mapperX.create(entityXY);
		mapperY.create(entityXY);

		world.process();
		assertEquals(2, counterX.insertedEntities);

		assertEquals(0, counterX.removedEntities);
		
		entityX.deleteFromWorld();
		world.process();
		assertEquals(1, counterX.removedEntities);
		
		entityY.deleteFromWorld();
		world.process();
		assertEquals(1, counterX.removedEntities);
		
		entityXY.deleteFromWorld();
		world.process();
		assertEquals(2, counterX.removedEntities);
	}

	static class SubscribingManager extends Manager
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

	static class TestManager extends Manager {
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

							assertTrue(world.em.isActive(entityId));
						}
					}

					@Override
					public void removed(IntBag entities) {
						int[] data = entities.getData();
						for (int i = 0; i < entities.size(); i++) {
							int entityId = data[i];
							assertEquals(0, entityId);
							assertTrue(world.em.isActive(entityId));
						}
					}
				});
			}
		}
	
	static class CompXCounterSystem extends EntitySystem {
		
		public int insertedEntities = 0;
		public int removedEntities = 0;
		
		public CompXCounterSystem()	{
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void processSystem() {}
		
		@Override
		public void inserted(Entity entity) {
			insertedEntities++;
		}
		
		@Override
		public void removed(Entity entity) {
			removedEntities++;
		}

	}

}