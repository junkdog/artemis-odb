package com.artemis.component;

import static org.junit.Assert.assertEquals;

import com.artemis.*;
import org.junit.Before;
import org.junit.Test;

import com.artemis.systems.EntityProcessingSystem;
import com.artemis.util.Vec2f;

/**
 * The weaving only happens for normal resources, not test classes.
 */
@SuppressWarnings("static-method")
public class WorldStructWeavingBase {
	
	private World world;

	@Before
	public void setup() {
		world = new World(new WorldConfiguration()
				.setSystem(new EntitySystemA())
				.setSystem(new EntitySystemB()));

		int e1 = world.createEntity();
		int e2 = world.createEntity();
		
		EntityEdit edit1 = EntityHelper.edit(world, e1);
		EntityEdit edit2 = EntityHelper.edit(world, e2);
		initComponent(edit1.create(StructComponentA.class));
		initComponent(edit1.create(Position.class));
		initComponent(edit2.create(StructComponentA.class));
		initComponent(edit2.create(Position.class));
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
			world.process();
		}
	}
	
	public static class EntitySystemA extends EntityProcessingSystem {

		private ComponentMapper<StructComponentA> mapper;
		int iteration = 0;
		
		@SuppressWarnings("unchecked")
		public EntitySystemA() {
			super(Aspect.all(StructComponentA.class));
		}
		
		@Override
		protected void begin() {
			iteration++;
		}

		@Override
		protected void process(int e) {
			int mod = (e % 2 == 0) ? 1 : 2;
			
			StructComponentA component = mapper.get(e);
			component.x += 10 * mod;
			component.y *= -10 * mod;
			component.z *= 20 * mod;
			component.something += ((e + 1) * mod);
			
			component = mapper.get(e);
			assertEquals(1 + iteration * 10f * mod, component.x, 0.1);
			assertEquals(2 * Math.pow(-10f * mod, iteration), component.y, 0.1);
			assertEquals(3 * Math.pow(20f * mod, iteration), component.z, 0.1);
			assertEquals(4 + ((e + 1) * iteration * mod), component.something);
		}
	}
	
	public static class EntitySystemB extends EntityProcessingSystem {
		
		private ComponentMapper<Position> mapper;
		int iteration = 0;
		
		@SuppressWarnings("unchecked")
		public EntitySystemB() {
			super(Aspect.all(Position.class));
		}
		
		@Override
		protected void begin() {
			iteration++;
		}
		
		@Override
		protected void process(int e) {
			int mod = (e % 2 == 0) ? 1 : 2;
			
			Position pos = mapper.get(e);
			pos.add(new Vec2f(10 * mod, 0));
			pos.xy(pos.x, pos.y * -10 * mod);
			
			pos = mapper.get(e);
			assertEquals(3 + iteration * 10f * mod, pos.x, 0.1);
			assertEquals(7 * Math.pow(-10f * mod, iteration), pos.y, 0.1);
		}
	}
}
