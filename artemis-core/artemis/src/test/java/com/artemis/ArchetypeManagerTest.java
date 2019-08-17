package com.artemis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

public class ArchetypeManagerTest {
	private World world;
	private Es1 es1;
	private Es2 es2;
	private Archetype arch1;
	private Archetype arch2;
	private Archetype arch3;
	private EntityFactory factory;

	@Before
	public void init() {
		world = new World(new WorldConfiguration()
				.setSystem(new EntityFactory())
				.setSystem(new Es1())
				.setSystem(new Es2()));

		world.inject(this);
	}
	
	@Test
	public void test_composition_id() {
		assertEquals(0, arch1.transmuter.compositionId);
		assertEquals(1, arch2.transmuter.compositionId);
		assertEquals(2, arch3.transmuter.compositionId);
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

	private void archetypeEntity(Archetype arch, int s) {
		for (int i = 0; s > i; i++) {
			world.createEntity(arch);
		}
	}
	
	private class EntityFactory extends Manager {
		
		@Override
		protected void initialize() {
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
	}
	
	public static class Es1 extends EntityProcessingSystem {

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
	
	public static class Es2 extends EntityProcessingSystem {
		
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
