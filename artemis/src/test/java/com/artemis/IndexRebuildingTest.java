package com.artemis;


import org.junit.Assert;
import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.systems.EntityProcessingSystem;

@SuppressWarnings("static-method")
public class IndexRebuildingTest {
	
	@Test
	public void rebuild_indices() {
		World world = new World(new WorldConfiguration().maxRebuiltIndicesPerTick(1));
		world.setSystem(new ES1());
		world.setSystem(new ES2());
		world.setSystem(new ES3());
		world.initialize();
		
		for (int i = 0; 100 > i; i++)
			create(world);
		
		world.process();
		Assert.assertEquals(1, world.rebuiltIndices);
		world.process();
		Assert.assertEquals(1, world.rebuiltIndices);
		world.process();
		Assert.assertEquals(1, world.rebuiltIndices);
		world.process();
		Assert.assertEquals(0, world.rebuiltIndices);
	}

	private static void create(World w) {
		Entity e = w.createEntity();
		e.createComponent(ComponentX.class);
		e.addToWorld();
	}
	
	private static class ES1 extends EntityProcessingSystem {

		@SuppressWarnings("unchecked")
		public ES1() {
			super(Aspect.getAspectForAll(ComponentX.class));
		}
		
		@Override
		protected void process(Entity e) {}
		
	}
	
	private static class ES2 extends EntityProcessingSystem {
		
		@SuppressWarnings("unchecked")
		public ES2() {
			super(Aspect.getAspectForAll(ComponentX.class));
		}
		
		@Override
		protected void process(Entity e) {}
		
	}
	
	private static class ES3 extends EntityProcessingSystem {
		
		@SuppressWarnings("unchecked")
		public ES3() {
			super(Aspect.getAspectForAll(ComponentX.class));
		}
		
		@Override
		protected void process(Entity e) {}
		
	}
}
