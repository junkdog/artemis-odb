package com.artemis.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.PooledComponent;
import com.artemis.World;

public class PolyConstructorTest {
	
	@Test @SuppressWarnings("static-method")
	public void pooled_class_with_many_constructors() throws Exception {
		World world = new World();
		world.initialize();
		
		Entity e1 = world.createEntity();
		PolyConstructor pooled1 = e1.createComponent(PolyConstructor.class);
		assertEquals(PooledComponent.class, pooled1.getClass().getSuperclass());

		PolyConstructor pooled2 = new PolyConstructor(420);
		assertEquals(PooledComponent.class, pooled2.getClass().getSuperclass());
	}
}
