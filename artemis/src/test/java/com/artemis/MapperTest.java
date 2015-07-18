package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class MapperTest {
	
	private World world;
	private MappedSystem mappedSystem;
	private MappedManager mappedManager;
	private Entity entity;

	@Before
	public void init() {
		world = new World(new WorldConfiguration()
				.setSystem(new MappedSystem())
				.setManager(new MappedManager()));
		
		world.inject(this);
		
		entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.create(ComponentX.class);
		edit.create(ComponentY.class);
		
		world.process();
	}
	
	@Test
	public void systems_support_mapper_annotation() {
		assertNotNull(mappedSystem.x);
		assertNotNull(mappedSystem.y);
		
		assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
		assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
	}
	
	@Test
	public void managers_support_mapper_annotation() {
		assertNotNull(mappedManager.x);
		assertNotNull(mappedManager.y);
		
		assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
		assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
	}
	
	@Wire
	private static class MappedSystem extends EntityProcessingSystem {
		private ComponentMapper<ComponentX> x;
		private ComponentMapper<ComponentY> y;
		
		@SuppressWarnings("unchecked")
		public MappedSystem() {
			super(Aspect.all(ComponentX.class, ComponentY.class));
		}

		@Override
		protected void process(Entity e) {}
		
	}
	
	@Wire
	private static class MappedManager extends Manager {
		private ComponentMapper<ComponentX> x;
		private ComponentMapper<ComponentY> y;
		
		@Override
		protected void initialize() {}
	}
}
