package com.artemis.system;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.EntitySystem;

@SuppressWarnings("static-method")
public class OptimizedSystemTest {
	
	@Test
	public void fully_optimized_entity_system() {
		Assert.assertEquals(EntitySystem.class, OptimizedSystem.class.getSuperclass());
		
		Method m = processMethod(OptimizedSystem.class);
		assertEquals(PRIVATE, m.getModifiers() & PRIVATE);
	}
	

	@Test
	public void safely_optimized_entity_system() {
		assertEquals(EntitySystem.class, OptimizedSystemSafe.class.getSuperclass());
		
		Method m = processMethod(OptimizedSystemSafe.class);
		assertEquals(PROTECTED, m.getModifiers() & PROTECTED);
	}
	
	private static Method processMethod(Class<?> klazz) {
		try {
			return klazz.getDeclaredMethod("process", Entity.class);
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		}
		return null;
	}
}
