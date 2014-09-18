package com.artemis.component;

import static java.lang.reflect.Modifier.PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;

/**
 * The weaving only happens for normal resources, not test classes.
 */
public class StructTestBase extends PackedWeavingTest {
	
	private StructComponentA packed;

	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.edit().create(StructComponentA.class);
		packed = e2.edit().create(StructComponentA.class);
	}
	
	@Override
	Class<?> componentType() {
		return StructComponentA.class;
	}
	
	@Override
	ComponentMapper<?> getMapper() {
		return world.getMapper(StructComponentA.class);
	}
	
	@Test
	public void packed_component_has_backing_array() throws Exception {
		Field data = field("$data");
		int size = field("$_SIZE_OF").getInt(null);
		assertEquals(15, size);
		
		assertEquals(PRIVATE, data.getModifiers());
		assertEquals(fieldType(), data.getType());
		assertEquals(128 * size, ((ByteBuffer)data.get(packed)).capacity());
		
		Method grow = method("$grow", int.class);
		grow.invoke(packed, 256 * size);
		assertEquals(256 * size, ((ByteBuffer)data.get(packed)).capacity());
	}

	@Test
	public void packed_component_init_all_setter() throws Exception {
		ComponentMapper<StructComponentA> mapper = world.getMapper(StructComponentA.class);
		mapper.get(e1).setXyz(4, 2, 0);
		mapper.get(e1).flag = true;
		mapper.get(e1).something = 0xfff;
		
		
		assertEquals(4, mapper.get(e1).x, 0.01f);
		assertEquals(2, mapper.get(e1).y, 0.01f);
		assertEquals(0, mapper.get(e1).z, 0.01f);
		assertTrue(mapper.get(e1).flag);
		assertEquals(0, mapper.get(e1).z, 0.01f);
	}
	
	@Test 
	public void packed_component_replaces_field_access_with_backing_array() throws Exception {
		ComponentMapper<StructComponentA> mapper = world.getMapper(StructComponentA.class);
		mapper.get(e1).setXyz(4, 3, 5);
		mapper.get(e2).setXyz(2, 1, 6);
		mapper.get(e1).flag = false;
		mapper.get(e2).flag = true;
		
		@SuppressWarnings("unused")
		ByteBuffer data = (ByteBuffer)field("$data").get(packed);
		
		assertEquals(4, mapper.get(e1).x, 0.001);
		assertEquals(3, mapper.get(e1).y, 0.001);
		assertEquals(5, mapper.get(e1).z, 0.001);
		
		assertEquals(2, mapper.get(e2).x, 0.001);
		assertEquals(1, mapper.get(e2).y, 0.001);
		assertEquals(6, mapper.get(e2).z, 0.001);
		
		assertFalse(mapper.get(e1).flag);
		assertTrue(mapper.get(e2).flag);
		
		try {
			StructComponentA.class.getDeclaredField("x");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
		try {
			StructComponentA.class.getDeclaredField("y");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
	}
	
	@Test
	public void packed_component_sub_value() throws Exception {
		ComponentMapper<StructComponentA> mapper = world.getMapper(StructComponentA.class);
		mapper.get(e1).setXyz(10, -10, 0);
		mapper.get(e2).setXyz(20, -20, 0);
		
		mapper.get(e1).x -= 30;
		mapper.get(e2).x -= -10;
		
		assertEquals(-20, mapper.get(e1).x, 0.001);
		assertEquals(30, mapper.get(e2).x, 0.001);
	}
	
	@Test
	public void packed_component_mul_value() throws Exception {
		ComponentMapper<StructComponentA> mapper = world.getMapper(StructComponentA.class);
		mapper.get(e1).setXyz(10, 20, 105);
		mapper.get(e2).setXyz(30, 40, 10);
		
		mapper.get(e1).something = 512;
		mapper.get(e2).something = 128;
		
		mapper.get(e1).something *= 4;
		mapper.get(e2).something *= 2;
		
		mapper.get(e1).z *= 4;
		mapper.get(e2).z *= -10;
		
		assertEquals(420, mapper.get(e1).z, 0.001);
		assertEquals(-100, mapper.get(e2).z, 0.001);
		assertEquals(2048, mapper.get(e1).something);
		assertEquals(256, mapper.get(e2).something);
	}
	
	@Test
	public void packed_component_div_value() throws Exception {
		ComponentMapper<StructComponentA> mapper = world.getMapper(StructComponentA.class);
		mapper.get(e1).setXyz(10, 20, 55);
		mapper.get(e2).setXyz(30, 40, 10);
		
		mapper.get(e1).y /= 4;
		mapper.get(e2).y /= 5;
		
		assertEquals(5, mapper.get(e1).y, 0.001);
		assertEquals(8, mapper.get(e2).y, 0.001);
	}
}
