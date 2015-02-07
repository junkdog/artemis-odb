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
		world = new World();
		es = world.setSystem(new ES1());
		world.initialize();

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

		assertTrue("compositionId=" + e2.getCompositionId(), 2 != e2.getCompositionId());
		assertEquals(e1.getCompositionId(), e2.getCompositionId());

		assertNotNull(e1.getComponent(ComponentX.class));
		assertNotNull(e1.getComponent(Packed.class));
		assertNotNull(e1.getComponent(ReusedComponent.class));
		assertNull(e1.getComponent(ComponentY.class));
	}

	@Test
	public void transmute_twice() {
		Entity e = createEntity(ComponentY.class, ReusedComponent.class);
		world.process();

		assertEquals(2, e.getCompositionId());

		transmuter1.transmute(e);
		assertEquals(1, e.getCompositionId());

		transmuter3.transmute(e);
		assertEquals(3, e.getCompositionId());
	}


	@Test
	public void entity_insertion_removal() {
		Entity e = world.createEntity();
		world.process();
		transmuter3.transmute(e);
		world.process();

		assertEquals(1, es.getActives().size());

		transmuter1.transmute(e);
		world.process();

		assertEquals(0, es.getActives().size());
	}

	private Entity createEntity(Class<? extends Component>... components) {
		Entity e = world.createEntity();
		EntityEdit edit = e.edit();
		for (Class<? extends Component> c : components)
			edit.create(c);

		return e;
	}

	private static class ES1 extends EntityProcessingSystem {
		public ES1() {
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void process(Entity e) {}
	}
}
