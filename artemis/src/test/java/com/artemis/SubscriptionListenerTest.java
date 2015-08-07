package com.artemis;


import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubscriptionListenerTest {

	@Test
	public void subscriptionlistener_inform_about_first_entities_test() {
		Es1 es1 = new Es1();
		Es2 es2 = new Es2();
		World world = new World(new WorldConfiguration()
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
		World w = new World(new WorldConfiguration()
				.setSystem(lsp1)
				.setSystem(lsp2));

		EntityTransmuter transmuter = new EntityTransmuterFactory(w)
				.add(ComponentX.class)
				.build();

		EntityEdit ee = w.createEntity().edit();
		ee.create(ComponentY.class);

		transmuter.transmute(ee.getEntity());

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
		World w = new World(new WorldConfiguration()
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
		World w = new World(new WorldConfiguration()
				.setSystem(lsp1)
				.setSystem(lsp2));

		Archetype archetype = new ArchetypeBuilder()
				.add(ComponentY.class)
				.build(w);

		EntityTransmuter transmuter = new EntityTransmuterFactory(w)
				.add(ComponentX.class)
				.build();

		Entity e = w.createEntity(archetype);
		transmuter.transmute(e);

		w.process();

		assertEquals(1, lsp2.insertedEntities.cardinality());
		assertEquals(1, lsp2.processedEntities.cardinality());
		assertEquals(1, lsp1.insertedEntities.cardinality());
		assertEquals(1, lsp1.processedEntities.cardinality());
	}


	private static class LeSystemPetite extends EntityProcessingSystem {
		private BitSet processedEntities = new BitSet();
		private BitSet insertedEntities = new BitSet();

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
		protected void process(Entity e) {
			processedEntities.set(e.id);
		}
	}

	private static class LeSystemPetite2 extends EntityProcessingSystem {
		private BitSet processedEntities = new BitSet();
		private BitSet insertedEntities = new BitSet();

		public LeSystemPetite2() {
			super(Aspect.all(ComponentY.class));
		}

		@Override
		protected void inserted(int entityId) {
			insertedEntities.set(entityId);
		}

		@Override
		protected void process(Entity e) {
			processedEntities.set(e.id);
		}

		@Override
		protected void removed(int entityId) {
			insertedEntities.set(entityId, false);
		}
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
