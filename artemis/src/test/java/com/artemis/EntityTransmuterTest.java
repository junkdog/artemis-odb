package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.Packed;
import com.artemis.component.ReusedComponent;
import com.artemis.systems.EntityProcessingSystem;
import org.junit.Before;
import org.junit.Test;

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
	public void transmuting_entities() {
		int e1 = createEntity(ComponentY.class, ReusedComponent.class);
		int e2 = createEntity(ComponentY.class, ReusedComponent.class);
		world.process();
		assertEquals(2, EntityHelper.getCompositionId(world, e1));

		transmuter3.transmute(e1);

		// manually applying transmuter to e2
		EntityEdit edit = EntityHelper.edit(world, e2);
		edit.create(ComponentX.class);
		edit.create(Packed.class);
		edit.remove(ComponentY.class);

		world.process();

		assertTrue("compositionId=" + EntityHelper.getCompositionId(world, e2), 2 != EntityHelper.getCompositionId(world, e2));
		assertEquals(EntityHelper.getCompositionId(world, e1), EntityHelper.getCompositionId(world, e2));

		assertNotNull(EntityHelper.getComponent(ComponentX.class, world, e1));
		assertNotNull(EntityHelper.getComponent(Packed.class, world, e1));
		assertNotNull(EntityHelper.getComponent(ReusedComponent.class, world, e1));
		assertNull(EntityHelper.getComponent(ComponentY.class, world, e1));
	}

	@Test
	public void transmute_twice() {
		int e = createEntity(ComponentY.class, ReusedComponent.class);
		world.process();

		assertEquals(2, EntityHelper.getCompositionId(world, e));

		transmuter1.transmute(e);
		assertEquals(1, EntityHelper.getCompositionId(world, e));

		transmuter3.transmute(e);
		assertEquals(3, EntityHelper.getCompositionId(world, e));
	}


	@Test
	public void entity_insertion_removal() {
		int e = world.createEntity();
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

		AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
		EntitySubscription subscription = asm.get(Aspect.all(ComponentX.class));

		EntityHelper.edit(world, world.createEntity()).create(ReusedComponent.class);
		EntityHelper.edit(world, world.createEntity()).create(ReusedComponent.class);

		world.process();
		assertEquals(0, subscription.getEntities().size());
		world.process();
		assertEquals(2, subscription.getEntities().size());
	}

	private int createEntity(Class<? extends Component>... components) {
		int e = world.createEntity();
		EntityEdit edit = EntityHelper.edit(world, e);
		for (Class<? extends Component> c : components)
			edit.create(c);

		return e;
	}

	private static class ES1 extends EntityProcessingSystem {
		public ES1() {
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void process(int e) {}
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
		protected void process(int e) {
			if (xMapper.has(e)) {
				removeX.transmute(e);
			} else {
				addX.transmute(e);
			}
		}
	}
}
