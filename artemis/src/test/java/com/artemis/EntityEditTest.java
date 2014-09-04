package com.artemis;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.artemis.EntityEditPool.EntityEdit;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;

public class EntityEditTest {
	
	@Test
	public void basic_entity_edit_test() {
		World world = new World();
		LeManager lm = world.setManager(new LeManager());
		world.initialize();
		
		Entity e = world.createEntity();
		e.addToWorld();

		world.process();
		
		assertEquals(1, lm.added);
		assertEquals(0, lm.changed);
		
		EntityEdit edit = e.edit();
		edit.createComponent(ComponentX.class);
		edit.createComponent(ComponentY.class);
		
		world.process();
		
		assertEquals(1, lm.added);
		assertEquals(1, lm.changed);
	}
	
	private static class LeManager extends Manager {
		
		int added, changed;
		
		@Override
		public void changed(Entity e) {
			changed++;
		}
		
		@Override
		public void added(Entity e) {
			added++;
		}
	}
}
