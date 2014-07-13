package com.artemis;

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
		innerWorld.initialize();
		
		world = new World();
		world.setSystem(new InnerWorldProcessingSystem(innerWorld));
		world.initialize();
		
		world.createEntity().createComponent(ComponentX.class);
		innerWorld.createEntity().createComponent(ComponentY.class);
		createEntity(innerWorld);
		
		world.process();
		
		int xIndexOuter = 
				world.getComponentManager().typeFactory.getTypeFor(ComponentX.class).getIndex();
		int yIndexInner = 
				innerWorld.getComponentManager().typeFactory.getTypeFor(ComponentY.class).getIndex();
		
		Assert.assertEquals(xIndexOuter, yIndexInner);
	}

	private static void createEntity(World w) {
		Entity e = w.createEntity();
		e.createComponent(ComponentX.class);
		e.createComponent(ComponentY.class);
	}
	
	public static class InnerWorldProcessingSystem extends VoidEntitySystem {

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
}
