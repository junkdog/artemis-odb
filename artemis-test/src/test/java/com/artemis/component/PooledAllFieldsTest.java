package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.PooledComponent;
import com.artemis.World;

public class PooledAllFieldsTest {

	@Test @SuppressWarnings("static-method")
	public void pooled_class_transformation() throws Exception {
		World world = new World();
		world.initialize();
		
		Entity e = world.createEntity();
		PooledAllFields pooled = e.createComponent(PooledAllFields.class);
		assertEquals(PooledComponent.class, pooled.getClass().getSuperclass());
		
		Method reset = pooled.getClass().getMethod("reset");
		reset.setAccessible(true);
		reset.invoke(pooled);
		
		for (Field f : pooled.getClass().getFields()) {
			if (boolean.class == f.getType())
				assertTrue(f.getBoolean(pooled));
			else if (f.getType().isPrimitive())
				assertEquals(0l, f.getLong(pooled));
			else
				assertEquals(null, f.get(pooled));
		}
	}
}
