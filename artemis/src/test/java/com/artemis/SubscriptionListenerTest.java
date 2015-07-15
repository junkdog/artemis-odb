package com.artemis;


import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;
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

	@Test
	public void entity_components_changed_by_edit_and_transmuter_test() {
		World w = new World();
		LeSystemPetite lsp1 = w.setSystem(new LeSystemPetite());
		LeSystemPetite2 lsp2 = w.setSystem(new LeSystemPetite2());
		w.initialize();

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
		World w = new World();
		LeSystemPetite lsp1 = w.setSystem(new LeSystemPetite());
		LeSystemPetite2 lsp2 = w.setSystem(new LeSystemPetite2());
		w.initialize();

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
		World w = new World();
		LeSystemPetite lsp1 = w.setSystem(new LeSystemPetite());
		LeSystemPetite2 lsp2 = w.setSystem(new LeSystemPetite2());
		w.initialize();

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
		protected void inserted(Entity e) {
			insertedEntities.set(e.id);
		}

		@Override
		protected void removed(Entity e) {
			insertedEntities.set(e.id, false);
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
		protected void inserted(Entity e) {
			insertedEntities.set(e.id);
		}

		@Override
		protected void process(Entity e) {
			processedEntities.set(e.id);
		}

		@Override
		protected void removed(Entity e) {
			insertedEntities.set(e.id, false);
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
		public void inserted(ImmutableBag<Entity> entities) {
			for (Entity e : entities) {
				assertNotNull(mapper.get(e));
				hasInserted = true;
			}
		}

		@Override
		public void removed(ImmutableBag<Entity> entities) {}
	}
}
