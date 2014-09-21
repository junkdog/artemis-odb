package com.artemis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class EntityStateTest {
	private World world;
	private Entity e;
	private Management management;

	@Before
	public void init() {
		world = new World();
		management = world.setManager(new Management());
		world.initialize();
		
		e = world.createEntity();
	}
	
	@Test
	public void disable_enable_test() {
		e.disable();
		e.enable();
		world.process();
		
		Assert.assertEquals(1, management.state);
	}
	
	@Test
	public void enable_disable_test() {
		e.enable();
		e.disable();
		world.process();
		
		Assert.assertEquals(-1, management.state);
	}
	
	private static class Management extends Manager {
		int state = 0;
		
		@Override
		public void enabled(Entity e) {
			state++;
		}
		
		@Override
		public void disabled(Entity e) {
			state--;
		}
	}
}
