package com.artemis;

import static org.junit.Assert.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Mapper;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;

public class MapperTest {
	
	private World world;
	private MappedSystem mappedSystem;
	private MappedManager mappedManager;
	private Entity entity;

	@Before
	public void init() {
		world = new World();
		mappedSystem = world.setSystem(new MappedSystem());
		mappedManager = world.setManager(new MappedManager());
		
		world.initialize();
		
		entity = world.createEntity();
		entity.createComponent(ComponentX.class);
		entity.createComponent(ComponentY.class);
		entity.addToWorld();
		
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
	
	private static class MappedSystem extends EntityProcessingSystem {
		@Mapper private ComponentMapper<ComponentX> x;
		@Mapper private ComponentMapper<ComponentY> y;
		
		@SuppressWarnings("unchecked")
		public MappedSystem() {
			super(Aspect.getAspectForAll(ComponentX.class, ComponentY.class));
		}

		@Override
		protected void process(Entity e) {}
		
	}
	
	private static class MappedManager extends Manager {
		@Mapper private ComponentMapper<ComponentX> x;
		@Mapper private ComponentMapper<ComponentY> y;
		
		@Override
		protected void initialize() {}
	}
}
