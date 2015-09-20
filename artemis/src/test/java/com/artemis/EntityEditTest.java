package com.artemis;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;

public class EntityEditTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void basic_entity_edit_test() {
		LeManager lm = new LeManager();
		World world = new World(new WorldConfiguration()
				.setSystem(lm));

		int e = world.createEntity();
		world.process();
		
		assertEquals(1, lm.added);
		assertEquals(0, lm.changed);
		
		EntityEdit edit = EntityHelper.edit(world, e);
		edit.create(ComponentX.class);
		edit.create(ComponentY.class);
		
		world.process();
		
		assertEquals(1, lm.added);
		assertEquals(1, lm.changed);
	}
	
	@Test
	public void test_composition_identity_simple_case() {
		World world = new World();

		int e = world.createEntity();
		world.process();
		assertEquals(1, EntityHelper.getCompositionId(world, e));
	}
	
	@Test
	public void test_composition_identity() {
		World world = new World();

		int e = world.createEntity();
		assertEquals(1, EntityHelper.getCompositionId(world, e));
	}
	
	private static class LeManager extends Manager {
		
		int added, changed;
		
		@Override
		public void changed(int e) {
			changed++;
		}
		
		@Override
		public void added(int e) {
			added++;
		}
	}
}
