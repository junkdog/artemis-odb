package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;

public class DoublePackedWeavingTest extends PackedWeavingTest {
	
	private TransPackedDouble packed;

	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.createComponent(TransPackedDouble.class);
		packed = e2.createComponent(TransPackedDouble.class);
	}

	@Override
	int fieldCount() {
		return 1;
	}

	@Override
	Class<?> fieldType() {
		return double[].class;
	}
	
	@Override
	Class<?> componentType() {
		return TransPackedDouble.class;
	}
	
	@Override
	ComponentMapper<?> getMapper() {
		return world.getMapper(TransPackedDouble.class);
	}
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		Field sizeOf = field("$_SIZE_OF");
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(fieldCount(), sizeOf.getInt(packed));
	}
	
	@Test
	public void packed_component_has_backing_array() throws Exception {
		Field data = field("$data");
		
		assertEquals(PRIVATE | STATIC, data.getModifiers());
		assertEquals(fieldType(), data.getType());
		assertEquals(64 * fieldCount(), ((double[])data.get(null)).length);
		
		Method grow = method("$grow");
		grow.invoke(packed);
		assertEquals(64 * fieldCount() * 2, ((double[])data.get(null)).length);
	}
	
	@Test 
	public void packed_component_replaces_field_access_with_backing_array() throws Exception {
		ComponentMapper<TransPackedDouble> mapper = world.getMapper(TransPackedDouble.class);
		mapper.get(e1).x(1);
		mapper.get(e2).x(3);
		
		assertEquals(1f, mapper.get(e1).x(), .001f);
		assertEquals(3f, mapper.get(e2).x(), .001f);
		
		try {
			TransPackedDouble.class.getDeclaredField("x");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
		try {
			TransPackedDouble.class.getDeclaredField("y");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
	}
}
