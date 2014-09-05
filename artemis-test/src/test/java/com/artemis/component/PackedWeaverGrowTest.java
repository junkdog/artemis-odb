package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;

public class PackedWeaverGrowTest {
	
	@Test @SuppressWarnings("static-method")
	public void packed_weaver_components_grow_correctly() throws Exception {
		World world = new World();
		world.initialize();
		
		for (int i = 0; 2048 > i; i++)
			createEntity(world);
		
		Entity last = createEntity(world);
		last.createComponent(SimpleComponent.class).set(420);
		
		world.process();
		
		SimpleComponent component = last.getComponent(SimpleComponent.class);
		assertNotNull(component);
		assertEquals(420, component.get());
		assertCapacity(128 * 8, component);
	}

	private static Entity createEntity(World w) {
		Entity e = w.createEntity();
		e.addToWorld();
		return e;
	}
	
	private static void assertCapacity(int minCapacity, SimpleComponent c) throws Exception {
		Field dataField = c.getClass().getDeclaredField("$data");
		dataField.setAccessible(true);
		ByteBuffer buffer = (ByteBuffer) dataField.get(c);
		assertTrue("reported capacity: " + buffer.capacity(), buffer.capacity() > minCapacity);
	}
}
