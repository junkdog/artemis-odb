package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;

public abstract class PackedWeavingTest {
	
	World world;
	Entity e1, e2;
	TransPackedFloat packed;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
		
		e1 = world.createEntity();
		e1.addToWorld();
		
		e2 = world.createEntity();
		e2.addToWorld();
	}
	
	abstract int getFieldCount();
	abstract Class<?> componentType();
	abstract Class<?> getFieldType();
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		Field sizeOf = field("$_SIZE_OF");
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(getFieldCount(), sizeOf.getInt(packed));
	}
	
	public void packed_component_has_offset() throws Exception {
		Field offset = field("$offset");
		
		assertEquals(PRIVATE, offset.getModifiers());
		assertEquals(int.class, offset.getType());
	}
	
	@Test
	public void packed_component_updates_offset() throws Exception {
		assertEquals(0, getOffset(e1));
		assertNotEquals(getOffset(e1), getOffset(e2));
	}
	
	private int getOffset(Entity e) throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		return field("$offset").getInt(mapper.get(e));
	}
	
	Method method(String name) throws SecurityException, NoSuchMethodException {
		Method m = componentType().getDeclaredMethod(name);
		assertNotNull(m);
		m.setAccessible(true);
		return m;
	}
	
	Field field(String name) throws NoSuchFieldException {
		Field f = componentType().getDeclaredField(name);
		assertNotNull(f);
		f.setAccessible(true);
		return f;
	}
}
