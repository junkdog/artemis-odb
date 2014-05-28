package com.artemis.component;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.junit.Before;
import org.junit.Test;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.PackedComponent;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.util.Vec2f;

/**
 * The weaving only happens for normal resources, not test classes.
 */
public class WorldStructWeavingBase {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.setSystem(new EntitySystemA());
		world.setSystem(new EntitySystemB());
		world.initialize();
		
		Entity e1 = world.createEntity();
		Entity e2 = world.createEntity();
		
		initComponent(e1.createComponent(StructComponentA.class));
		initComponent(e1.createComponent(Position.class));
		initComponent(e2.createComponent(StructComponentA.class));
		initComponent(e2.createComponent(Position.class));
		
		e1.addToWorld();
		e2.addToWorld();
	}

	private static void initComponent(Position pos) {
		pos.x = 3;
		pos.y = 7;
	}

	private static void initComponent(StructComponentA packed) {
		packed.x = 1;
		packed.y = 2;
		packed.z = 3;
		packed.flag = true;
		packed.something = 4;
	}
	
	@Test
	public void component_is_woven() {
		assertEquals(PackedComponent.class, StructComponentA.class.getSuperclass());
	}

	Class<?> componentType() {
		return StructComponentA.class;
	}

	ComponentMapper<?> getMapper() {
		return world.getMapper(StructComponentA.class);
	}
	
	@Test
	public void run_the_world() {
		for (int i = 0; 9 > i; i++) {
			System.out.println("iteration " + i);
			world.process();
		}
	}
	
	@Wire
	public static class EntitySystemA extends EntityProcessingSystem {

		private ComponentMapper<StructComponentA> mapper;
		int iteration = 0;
		
		@SuppressWarnings("unchecked")
		public EntitySystemA() {
			super(Aspect.getAspectForAll(StructComponentA.class));
		}
		
		@Override
		protected void begin() {
			iteration++;
		}

		@Override
		protected void process(Entity e) {
			int mod = (e.getId() % 2 == 0) ? 1 : 2;
			
			StructComponentA component = mapper.get(e);
			component.x += 10 * mod;
			component.y *= -10 * mod;
			component.z *= 20 * mod;
			component.something += ((e.getId() + 1) * mod);
			
			component = mapper.get(e);
			System.out.println(component);
			System.out.println(component);
			System.out.println(component.x);
			System.out.println(component.y);
			assertEquals(1 + iteration * 10f * mod, component.x, 0.1);
			assertEquals(2 * Math.pow(-10f * mod, iteration), component.y, 0.1);
			assertEquals(3 * Math.pow(20f * mod, iteration), component.z, 0.1);
			assertEquals(4 + ((e.getId() + 1) * iteration * mod), component.something);
		}
	}
	
	@Wire
	public static class EntitySystemB extends EntityProcessingSystem {
		
		private ComponentMapper<Position> mapper;
		int iteration = 0;
		
		@SuppressWarnings("unchecked")
		public EntitySystemB() {
			super(Aspect.getAspectForAll(Position.class));
		}
		
		@Override
		protected void begin() {
			iteration++;
		}
		
		@Override
		protected void process(Entity e) {
			int mod = (e.getId() % 2 == 0) ? 1 : 2;
			
			Position pos = mapper.get(e);
//			pos.x += 10 * mod;
			pos.add(new Vec2f(10 * mod, 0));
//			pos.y *= -10 * mod;
			pos.xy(pos.x, pos.y * -10 * mod);
			
			pos = mapper.get(e);
			System.out.println(pos);
			System.out.println(pos.x);
			System.out.println(pos.y);
			assertEquals(3 + iteration * 10f * mod, pos.x, 0.1);
			assertEquals(7 * Math.pow(-10f * mod, iteration), pos.y, 0.1);
		}
	}
}
