package com.artemis.system.iterating;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("static-method")
public class IntOptimizedSystemTest {
	
	@Test
	public void fully_optimized_entity_system() {
		Assert.assertEquals(EntitySystem.class, IntOptimizedSystem.class.getSuperclass());
		
		Method m = processMethod(IntOptimizedSystem.class);
		assertEquals(PRIVATE, m.getModifiers() & PRIVATE);
	}
	

	@Test
	public void safely_optimized_entity_system() {
		assertEquals(EntitySystem.class, IntOptimizedSystemSafe.class.getSuperclass());
		
		Method m = processMethod(IntOptimizedSystemSafe.class);
		assertEquals(PROTECTED, m.getModifiers() & PROTECTED);
	}
	
	@Test
	public void fully_optimized_entity_system_with_additional_references() {
		Assert.assertEquals(EntitySystem.class, IntOptimizedSystemAdditional.class.getSuperclass());

		Method m = processMethod(IntOptimizedSystemAdditional.class);
		assertEquals(PRIVATE, m.getModifiers() & PRIVATE);

		World world = new World(new WorldConfiguration()
				.setSystem(new IntOptimizedSystemAdditional()));

		world.process();
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
