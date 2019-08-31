package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.artemis.component.ReusedComponent;
import com.artemis.utils.Bag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

public class ArchetypeTest {
	private World world;
	private Es1 es1;
	private Es2 es2;
	private Archetype arch1;
	private Archetype arch2;
	private Archetype arch3;

	@Before
	public void init() {
		world = new World(new WorldConfiguration()
				.setSystem(new Es1())
				.setSystem(new Es2()));
		world.inject(this);

		arch1 = new ArchetypeBuilder()
			.build(world);
		arch2 = new ArchetypeBuilder()
			.add(ComponentX.class)
			.add(ComponentY.class)
			.build(world);
		arch3 = new ArchetypeBuilder()
			.add(ComponentX.class)
			.build(world);
	}

	@Test
	public void test_composition_id() {
		assertEquals(0, arch1.transmuter.compositionId);
		assertEquals(1, arch2.transmuter.compositionId);
		assertEquals(2, arch3.transmuter.compositionId);
	}

	@Test
	public void test_inherited_archetypes_and_composition_resolution() {
		Archetype arch4 = new ArchetypeBuilder(arch2).build(world);
		Archetype arch5 = new ArchetypeBuilder(arch2).remove(ComponentY.class).build(world);
		Archetype arch6 = new ArchetypeBuilder(arch2).remove(ComponentX.class).build(world);

		assertEquals(arch2.transmuter.compositionId, arch4.transmuter.compositionId);
		assertEquals(arch3.transmuter.compositionId, arch5.transmuter.compositionId);
		assertEquals(3, arch6.transmuter.compositionId);
	}

	@Test
	public void test_adding_to_systems() {
		archetypeEntity(arch1, 2); // never inserted
		archetypeEntity(arch2, 4); // es1
		archetypeEntity(arch3, 8); // es1 + 2

		world.process();

		assertEquals(12, es1.getSubscription().getEntities().size());
		assertEquals(8, es2.getSubscription().getEntities().size());

		world.process();
	}

	@Test
	public void create_many_entities_with_pooled_components() {
		World world = new World();
		Archetype archPooled = new ArchetypeBuilder()
				.add(ReusedComponent.class)
				.build(world);

		for (int i = 0; 256> i; i++) {
			world.createEntity(archPooled);
		}
	}

	@Test
	public void create_with_int_id() {
		World world = new World();
		Archetype archPooled = new ArchetypeBuilder()
				.add(ReusedComponent.class)
				.build(world);

		Assert.assertEquals(0, world.create(archPooled));
		Assert.assertEquals(1, world.create(archPooled));
		Assert.assertEquals(2, world.create(archPooled));
	}

	@Test
	public void testEntityCreationMod() {
		World world = new World();

		ComponentMapper<ComponentX> xMapper = world.getMapper(ComponentX.class);
		ComponentMapper<ComponentY> yMapper = world.getMapper(ComponentY.class);

		ArchetypeBuilder builder = new ArchetypeBuilder().add(ComponentX.class);
		Archetype archetype = builder.build(world);
		Entity entity = world.createEntity(archetype);
		entity.edit().create(ComponentY.class);
		world.process();

		assertNotNull(entity.getComponent(ComponentX.class));
		assertNotNull(entity.getComponent(ComponentY.class));
		assertNotNull(xMapper.get(entity));
		assertNotNull(yMapper.get(entity));
		//This is false, the bag only contains SpatialModifierComponent
		assertEquals(2, entity.getComponents(new Bag<Component>()).size());
	}

	private void archetypeEntity(Archetype arch, int s) {
		for (int i = 0; s > i; i++) {
			world.create(arch);
		}
	}

	private static class Es1 extends EntityProcessingSystem {

		private ComponentMapper<ComponentX> componentXMapper;

		@SuppressWarnings("unchecked")
		public Es1() {
			super(Aspect.all(ComponentX.class));
		}

		@Override
		protected void process(Entity e) {
			assertNotNull(componentXMapper.get(e));
		}
	}

	private static class Es2 extends EntityProcessingSystem {

		private ComponentMapper<ComponentX> componentXMapper;

		@SuppressWarnings("unchecked")
		public Es2() {
			super(Aspect.all(ComponentX.class).exclude(ComponentY.class));
		}

		@Override
		protected void process(Entity e) {
			assertNotNull(componentXMapper.get(e));
		}
	}
}
