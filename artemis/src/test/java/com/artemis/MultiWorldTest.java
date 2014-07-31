package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.VoidEntitySystem;

public class MultiWorldTest
{
	private World world;

	@Test
	public void uniqie_component_ids_per_world()
	{
		World innerWorld = new World();
		innerWorld.setSystem(new EmptySystem());
		innerWorld.initialize();
		
		world = new World();
		world.setSystem(new InnerWorldProcessingSystem(innerWorld));
		world.initialize();
		
		world.createEntity().createComponent(ComponentX.class);
		innerWorld.createEntity().createComponent(ComponentY.class);
		
		world.process();
		
		int xIndexOuter = 
				world.getComponentManager().typeFactory.getTypeFor(ComponentX.class).getIndex();
		int yIndexInner = 
				innerWorld.getComponentManager().typeFactory.getTypeFor(ComponentY.class).getIndex();
		
		assertEquals(xIndexOuter, yIndexInner);
	}
	
	@Test
	public void unique_system_bits_per_world() {
		World innerWorld = new World();
		VoidSystem innerVoid = innerWorld.setSystem(new VoidSystem());
		innerWorld.initialize();
		
		world = new World();
		world.setSystem(new InnerWorldProcessingSystem(innerWorld));
		VoidSystem outerVoid = world.setSystem(new VoidSystem());
		world.initialize();
		
		world.process();
		
		assertNotEquals(getSystemBit(innerVoid), getSystemBit(outerVoid));
	}
	
	private static int getSystemBit(EntitySystem es) {
		try {
			Field f = EntitySystem.class.getDeclaredField("systemIndex");
			f.setAccessible(true);
			return f.getInt(es);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (NoSuchFieldException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		}
		return -1;
	}

	private static void createEntity(World w) {
		Entity e = w.createEntity();
		e.createComponent(ComponentX.class);
		e.createComponent(ComponentY.class);
	}

	public static class EmptySystem
			extends VoidEntitySystem {

		@Override
		protected void processSystem() {
		}

	}

	public static class InnerWorldProcessingSystem
			extends VoidEntitySystem {

		private final World inner;

		public InnerWorldProcessingSystem(World inner) {
			super();
			this.inner = inner;
		}

		@Override
		protected void processSystem() {
			inner.delta = world.delta;
			inner.process();
		}
		
	}
	
	private static class VoidSystem extends VoidEntitySystem {
		@Override
		protected void processSystem() {}
	}
}
