package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.artemis.EntityHelper;
import com.artemis.World;

public class PackedWeaverGrowTest {
	
	@Test @SuppressWarnings("static-method")
	public void packed_weaver_components_grow_correctly() throws Exception {
		World world = new World();

		for (int i = 0; 2048 > i; i++)
			createEntity(world);
		
		int last = createEntity(world);
		EntityHelper.edit(world, last).create(SimpleComponent.class).set(420);
		
		world.process();
		
		SimpleComponent component = EntityHelper.getComponent(SimpleComponent.class, world, last);
		assertNotNull(component);
		assertEquals(420, component.get());
		assertCapacity(128 * 8, component);
	}

	private static int createEntity(World w) {
		return w.createEntity();
	}
	
	private static void assertCapacity(int minCapacity, SimpleComponent c) throws Exception {
		Field dataField = c.getClass().getDeclaredField("$data");
		dataField.setAccessible(true);
		ByteBuffer buffer = (ByteBuffer) dataField.get(c);
		assertTrue("reported capacity: " + buffer.capacity(), buffer.capacity() > minCapacity);
	}
}
