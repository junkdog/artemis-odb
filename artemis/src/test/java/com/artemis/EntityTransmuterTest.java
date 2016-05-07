package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.Packed;
import com.artemis.component.ReusedComponent;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import org.junit.Before;
import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.*;

public class EntityTransmuterTest {

	private World world;
	private ES1 es;
	private EntityTransmuter transmuter1;
	private EntityTransmuter transmuter3;

	@Before
	public void init() {
		world = new World(new WorldConfiguration()
				.setSystem(new ES1()));
		world.inject(this);

		transmuter3 = new EntityTransmuterFactory(world)
			.add(ComponentX.class)
			.add(Packed.class)
			.remove(ComponentY.class)
			.build();

		transmuter1 = new EntityTransmuterFactory(world)
			.remove(ComponentX.class)
			.remove(ComponentY.class)
			.remove(Packed.class)
			.remove(ReusedComponent.class)
			.build();
	}

	@Test
	public void transmute_deleted_entity_does_not_cancel_delete() {
		EntityManager em = world.getEntityManager();
		int id = world.create();

		world.process();
		assertTrue(em.isActive(id));

		world.delete(id);
		transmuter3.transmute(id);

		world.process();
		assertFalse(em.isActive(id));
	}

	@Test
	public void transmuting_entities() {
		Entity e1 = createEntity(ComponentY.class, ReusedComponent.class);
		Entity e2 = createEntity(ComponentY.class, ReusedComponent.class);
		world.process();
		assertEquals(2, e1.getCompositionId());

		transmuter3.transmute(e1);

		// manually applying transmuter to e2
		EntityEdit edit = e2.edit();
		edit.create(ComponentX.class);
		edit.create(Packed.class);
		edit.remove(ComponentY.class);

		world.process();
		world.process();

		assertTrue("compositionId=" + e2.getCompositionId(), 2 != e2.getCompositionId());
		assertEquals(e1.getCompositionId(), e2.getCompositionId());

		assertNotNull(e1.getComponent(ComponentX.class));
		assertNotNull(e1.getComponent(Packed.class));
		assertNotNull(e1.getComponent(ReusedComponent.class));
		assertNull(e1.getComponent(ComponentY.class));
		assertNotNull(e2.getComponent(ComponentX.class));
		assertNotNull(e2.getComponent(Packed.class));
		assertNotNull(e2.getComponent(ReusedComponent.class));
		assertNull(e2.getComponent(ComponentY.class));
	}

	@Test
	public void transmute_twice() {
		Entity e = createEntity(ComponentY.class, ReusedComponent.class);
		world.process();

		assertEquals(2, e.getCompositionId());

		transmuter1.transmute(e);
		assertEquals(0, e.getCompositionId());

		transmuter3.transmute(e);
		assertEquals(3, e.getCompositionId());
	}


	@Test
	public void entity_insertion_removal() {
		Entity e = world.createEntity();
		world.process();
		transmuter3.transmute(e);
		world.process();

		assertEquals(1, es.getSubscription().getEntities().size());

		transmuter1.transmute(e);
		world.process();

		assertEquals(0, es.getSubscription().getEntities().size());
	}

	@Test
	public void toggle_entities_single_component() {
		ES2 es2 = new ES2();
		World world = new World(new WorldConfiguration()
				.setSystem(es2));

		AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
		EntitySubscription subscription = asm.get(Aspect.all(ComponentX.class));

		world.createEntity().edit().create(ReusedComponent.class);
		world.createEntity().edit().create(ReusedComponent.class);

		world.process();
		assertEquals(2, subscription.getEntities().size());
		world.process();
		assertEquals(0, subscription.getEntities().size());
	}

	@Test
	public void deleted_transmuted_editors_never_inserted_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(SysTransmuter.class)
			.setSystem(SysSubscriber.class));

		world.createEntity().edit().create(ComponentX.class);
		world.process();
	}

	@Test
	public void operation_cancelled_when_pending_deletion_with_different_updates_ticks() {
		final World world = new World(new WorldConfiguration()
			.setSystem(new ES1()));


		EntitySubscription subscription = world.getAspectSubscriptionManager()
			.get(all(ComponentX.class, ComponentY.class));

		subscription.addSubscriptionListener(
			new EntitySubscription.SubscriptionListener() {
				private boolean insertedOnce;

				@Override
				public void inserted(IntBag entities) {
					if (!insertedOnce) {
						insertedOnce = true;
						world.delete(entities.get(0));
					} else {
						fail("entity was deleted, but a 2nd insertion was made, " +
							 "probably due to the removal in #removed");
					}
				}

				@Override
				public void removed(IntBag entities) {
					world.edit(entities.get(0)).remove(ReusedComponent.class);
				}
			});

		final int e = world.create();
		world.edit(e).create(ComponentX.class);
		world.edit(e).create(ComponentY.class);
		world.edit(e).create(ReusedComponent.class);

		world.process();
	}

	private Entity createEntity(Class<? extends Component>... components) {
		Entity e = world.createEntity();
		EntityEdit edit = e.edit();
		for (Class<? extends Component> c : components)
			edit.create(c);

		return e;
	}

	public static class SysTransmuter extends IteratingSystem {
		private EntityTransmuter transmuter;

		public SysTransmuter() {
			super(all(ComponentX.class));
		}

		@Override
		protected void initialize() {
			transmuter = new EntityTransmuterFactory(world)
				.add(ComponentY.class)
				.build();
		}

		@Override
		protected void process(int entityId) {
			transmuter.transmute(entityId);
			world.edit(entityId).create(ReusedComponent.class);
			world.delete(entityId);
		}
	}

	public static class SysSubscriber extends BaseEntitySystem {
		public SysSubscriber() {
			super(all(ComponentY.class));
		}

		@Override
		protected void processSystem() {}

		@Override
		protected void inserted(int entityId) {
			super.inserted(entityId);
			fail("entity is dead");
		}
	}

	private static class ES1 extends EntityProcessingSystem {
		public ES1() {
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void process(Entity e) {}
	}

	private static class ES2 extends EntityProcessingSystem {
		ComponentMapper<ComponentX> xMapper;
		private EntityTransmuter addX;
		private EntityTransmuter removeX;

		public ES2() {
			super(Aspect.all(ReusedComponent.class));
		}

		@Override
		protected void initialize() {
			addX = new EntityTransmuterFactory(world)
				.add(ComponentX.class)
				.build();

			removeX = new EntityTransmuterFactory(world)
					.remove(ComponentX.class)
					.build();
		}

		@Override
		protected void process(Entity e) {
			if (xMapper.has(e)) {
				removeX.transmute(e);
			} else {
				addX.transmute(e);
			}
		}
	}
}
