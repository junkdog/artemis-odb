package com.artemis.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.PackedComponent;
import com.artemis.World;

public class EmptyPackedTest {
	
	@Test @SuppressWarnings("static-method")
	public void empty_packed_shouldnt_reference_bytbuffer() throws Exception {
		World world = new World();
		world.initialize();
		
		Entity e1 = world.createEntity();
		EmptyPacked empty = e1.createComponent(EmptyPacked.class);
		assertEquals(PackedComponent.class, empty.getClass().getSuperclass());
	}
}
