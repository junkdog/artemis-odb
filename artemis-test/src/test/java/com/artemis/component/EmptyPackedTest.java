package com.artemis.component;

import static org.junit.Assert.assertEquals;

import com.artemis.EntityHelper;
import org.junit.Test;

import com.artemis.PackedComponent;
import com.artemis.World;

public class EmptyPackedTest {
	
	@Test @SuppressWarnings("static-method")
	public void empty_packed_shouldnt_reference_bytbuffer() throws Exception {
		World world = new World();

		int e1 = world.createEntity();
		EmptyPacked empty = EntityHelper.edit(world, e1).create(EmptyPacked.class);
		assertEquals(PackedComponent.class, empty.getClass().getSuperclass());
	}
}
