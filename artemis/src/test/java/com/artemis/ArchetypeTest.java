package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.artemis.component.ReusedComponent;
import com.artemis.utils.Bag;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class ArchetypeTest {
	private World world;
	private Es1 es1;
	private Es2 es2;
	private Archetype arch1;
	private Archetype arch2;
	private Archetype arch3;
	private Archetype archPooled;

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
		archPooled = new ArchetypeBuilder()
			.add(ReusedComponent.class)
			.build(world);
	}

	@Test
	public void test_composition_id() throws Exception {
		assertEquals(1, arch1.compositionId);
		assertEquals(2, arch2.compositionId);
		assertEquals(3, arch3.compositionId);
	}

	@Test
	public void test_archetypes_component_classes() throws Exception {
		assertEquals(0, arch1.types.length);
		assertEquals(2, arch2.types.length);
		assertEquals(ComponentX.class, arch2.types[0].getType());
		assertEquals(ComponentY.class, arch2.types[1].getType());
		assertEquals(1, arch3.types.length);
		assertEquals(ComponentX.class, arch3.types[0].getType());
	}

	@Test
	public void test_inherited_archetypes_and_composition_resolution() throws Exception {
		Archetype arch4 = new ArchetypeBuilder(arch2).build(world);
		Archetype arch5 = new ArchetypeBuilder(arch2).remove(ComponentY.class).build(world);
		Archetype arch6 = new ArchetypeBuilder(arch2).remove(ComponentX.class).build(world);

		assertEquals(arch2.compositionId, arch4.compositionId);
		assertEquals(arch3.compositionId, arch5.compositionId);
		assertEquals(4, arch6.compositionId);

		assertEquals(1, arch6.types.length);
		assertEquals(ComponentY.class, arch6.types[0].getType());
	}

	@Test
	public void test_adding_to_systems() {
		archetypeEntity(arch1, 2); // never inserted
		archetypeEntity(arch2, 4); // es1
		archetypeEntity(arch3, 8); // es1 + 2

		world.process();

		assertEquals(12, es1.getActives().size());
		assertEquals(8, es2.getActives().size());

		world.process();
	}

	@Test
	public void create_many_entities_with_pooled_components() {
		archetypeEntity(archPooled, 256);
	}

	@Test
	public void testEntityCreationMod() throws Exception {
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
			world.createEntity(arch);
		}
	}

	@Wire
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

	@Wire
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
