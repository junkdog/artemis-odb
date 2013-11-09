package com.artemis.component;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;

public class FloatPackedWeavingTest extends PackedWeavingTest {
	
	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.createComponent(TransPackedFloat.class);
		packed = e2.createComponent(TransPackedFloat.class);
	}
	

	@Override
	int getFieldCount() {
		return 2;
	}

	@Override
	Class<?> getFieldType() {
		return float[].class;
	}
	
	@Override
	Class<?> componentType() {
		return TransPackedFloat.class;
	}
	
	@Test
	public void packed_component_has_backing_array() throws Exception {
		Field data = field("$data");
		
		assertEquals(PRIVATE | STATIC, data.getModifiers());
		assertEquals(getFieldType(), data.getType());
		assertEquals(64, ((float[])data.get(null)).length);
		
		Method grow = method("$grow");
		grow.invoke(packed);
		assertEquals(64 * getFieldCount(), ((float[])data.get(null)).length);
	}
	
	@Test 
	public void packed_component_replaces_field_access_with_backing_array() throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		mapper.get(e1).x(1).y(2);
		mapper.get(e2).x(3).y(4);
		
		assertEquals(1f, mapper.get(e1).x(), .001f);
		assertEquals(2f, mapper.get(e1).y(), .001f);
		assertEquals(3f, mapper.get(e2).x(), .001f);
		assertEquals(4f, mapper.get(e2).y(), .001f);
		
		try {
			TransPackedFloat.class.getDeclaredField("x");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
		try {
			TransPackedFloat.class.getDeclaredField("y");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
	}
	
	@Test
	public void packed_component_add_to_value() throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		mapper.get(e1).x(1).y(2);
		mapper.get(e2).x(3).y(4);
		
		mapper.get(e1).addX(4);
		mapper.get(e2).addX(8);
		
		assertEquals(5f, mapper.get(e1).x(), .001f);
		assertEquals(2f, mapper.get(e1).y(), .001f);
		assertEquals(11f, mapper.get(e2).x(), .001f);
		assertEquals(4f, mapper.get(e2).y(), .001f);
		
		try {
			TransPackedFloat.class.getDeclaredField("x");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
		try {
			TransPackedFloat.class.getDeclaredField("y");
			fail("Failed to remove field from component");
		} catch (Exception e) { /* expected */ }
	}
}
