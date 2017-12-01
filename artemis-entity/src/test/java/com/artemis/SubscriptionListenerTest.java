package com.artemis;


import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import org.junit.Test;

import com.artemis.utils.BitVector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubscriptionListenerTest {

	@Test
	public void subscriptionlistener_inform_about_first_entities_test() {
		Es1 es1 = new Es1();
		Es2 es2 = new Es2();
		EntityWorld world = new EntityWorld(new WorldConfiguration()
				.setSystem(es1)
				.setSystem(es2));

		world.process();

		assertTrue(es2.hasInserted);

		world.process();
	}

	@Test
	public void entity_components_changed_by_edit_and_transmuter_test() {
		LeSystemPetite lsp1 = new LeSystemPetite();
		LeSystemPetite2 lsp2 = new LeSystemPetite2();
		EntityWorld w = new EntityWorld(new WorldConfiguration()
				.setSystem(lsp1)
				.setSystem(lsp2));

		EntityTransmuter transmuter = new EntityTransmuterFactory(w)
				.add(ComponentX.class)
				.build();

		EntityEdit ee = w.createEntity().edit();
		ee.create(ComponentY.class);

		transmuter.transmute(ee.getEntityId());

		w.process();

		assertEquals(1, lsp2.insertedEntities.cardinality());
		assertEquals(1, lsp2.processedEntities.cardinality());
		assertEquals(1, lsp1.insertedEntities.cardinality());
		assertEquals(1, lsp1.processedEntities.cardinality());
	}

	@Test
	public void entity_components_changed_by_archetype_and_edit_test() {
		LeSystemPetite lsp1 = new LeSystemPetite();
		LeSystemPetite2 lsp2 = new LeSystemPetite2();
		EntityWorld w = new EntityWorld(new WorldConfiguration()
				.setSystem(lsp1)
				.setSystem(lsp2));

		Archetype archetype = new ArchetypeBuilder()
				.add(ComponentY.class)
				.build(w);

		Entity e = w.createEntity(archetype);
		e.edit().create(ComponentX.class);

		w.process();

		assertEquals(1, lsp2.insertedEntities.cardinality());
		assertEquals(1, lsp2.processedEntities.cardinality());
		assertEquals(1, lsp1.insertedEntities.cardinality());
		assertEquals(1, lsp1.processedEntities.cardinality());
	}

	@Test
	public void entity_components_changed_by_archetype_and_transmuter_test() {
		LeSystemPetite lsp1 = new LeSystemPetite();
		LeSystemPetite2 lsp2 = new LeSystemPetite2();
		EntityWorld w = new EntityWorld(new WorldConfiguration()
				.setSystem(lsp1)
				.setSystem(lsp2));

		Archetype archetype = new ArchetypeBuilder()
				.add(ComponentY.class)
				.build(w);

		EntityTransmuter transmuter = new EntityTransmuterFactory(w)
				.add(ComponentX.class)
				.build();

		Entity e = w.createEntity(archetype);
		transmuter.transmute(e.id);

		w.process();

		assertEquals(1, lsp2.insertedEntities.cardinality());
		assertEquals(1, lsp2.processedEntities.cardinality());
		assertEquals(1, lsp1.insertedEntities.cardinality());
		assertEquals(1, lsp1.processedEntities.cardinality());
	}


	private static class LeSystemPetite extends IteratingSystem {
		private BitVector processedEntities = new BitVector();
		private BitVector insertedEntities = new BitVector();

		public LeSystemPetite() {
			super(Aspect.all(ComponentX.class, ComponentY.class));
		}

		@Override
		protected void inserted(int entityId) {
			insertedEntities.set(entityId);
		}

		@Override
		protected void removed(int entityId) {
			insertedEntities.set(entityId, false);
		}

		@Override
		protected void process(int e) {
			processedEntities.set(e);
		}
	}

	private static class LeSystemPetite2 extends EntityProcessingSystem {
		private BitVector processedEntities = new BitVector();
		private BitVector insertedEntities = new BitVector();

		public LeSystemPetite2() {
			super(Aspect.all(ComponentY.class));
		}

		@Override
		public void inserted(Entity e) {
			insertedEntities.set(e.getId());
		}

		@Override
		protected void process(Entity e) {
			processedEntities.set(e.id);
		}

		@Override
		public void removed(Entity e) {
			insertedEntities.set(e.getId(), false);
		}
	}


	public static class MyComponent extends Component {
	}

	private static class Es1 extends BaseSystem {
		private boolean isInitialized;

		@Override
		protected void processSystem() {
			if (!isInitialized) {
				world.edit(world.create()).create(MyComponent.class);
				isInitialized = true;
			}
		}
	}


	private static class Es2 extends BaseSystem implements SubscriptionListener {
		private ComponentMapper<MyComponent> mapper;
		private boolean hasInserted;

		@Override
		protected void processSystem() {
			assertTrue(hasInserted);
		}

		@Override
		protected void initialize() {
			AspectSubscriptionManager am = world.getAspectSubscriptionManager();
			am.get(Aspect.all(MyComponent.class)).addSubscriptionListener(this);
		}

		@Override
		public void inserted(IntBag entities) {
			for (int i = 0, s = entities.size(); s > i; i++) {
				assertNotNull(mapper.get(entities.get(i)));
				hasInserted = true;
			}
		}

		@Override
		public void removed(IntBag entities) {}
	}
}
