package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;
import com.artemis.util.Vec2f;

public class FloatPackedWeavingTest extends PackedWeavingTest {
	
	private TransPackedFloat packed;

	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.createComponent(TransPackedFloat.class);
		packed = e2.createComponent(TransPackedFloat.class);
	}
	
	@Override
	Class<?> componentType() {
		return TransPackedFloat.class;
	}
	
	@Override
	ComponentMapper<?> getMapper() {
		return world.getMapper(TransPackedFloat.class);
	}
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		Field sizeOf = field("$_SIZE_OF");
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(8, sizeOf.getInt(packed));
	}
	
	@Test
	public void packed_component_has_backing_array() throws Exception {
		Field data = field("$data");
		int size = field("$_SIZE_OF").getInt(null);
		
		assertEquals(PRIVATE, data.getModifiers());
		assertEquals(fieldType(), data.getType());
		assertEquals(128 * size, ((ByteBuffer)data.get(packed)).capacity());
		
		Method grow = method("$grow", int.class);
		grow.invoke(packed, 256 * size);
		assertEquals(256 * size, ((ByteBuffer)data.get(packed)).capacity());
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
	
	@Test
	public void packed_component_add_with_object() throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		Vec2f v1 = new Vec2f(1f, 2f);
		Vec2f v2 = new Vec2f(3f, 4f);
		
		mapper.get(e1).x(1f).y(1f);
		mapper.get(e2).x(1f).y(1f);
		
		mapper.get(e1).add(v1);
		mapper.get(e2).add(v2);
		
		String err = String.format("e1=%.0f,%.0f e2=%.0f,%.0f",
			mapper.get(e1).x(), mapper.get(e1).y(),
			mapper.get(e2).x(), mapper.get(e2).y());
		
		assertEquals(err, 2f, mapper.get(e1).x(), .001f);
		assertEquals(err, 3f, mapper.get(e1).y(), .001f);
		assertEquals(err, 4f, mapper.get(e2).x(), .001f);
		assertEquals(err, 5f, mapper.get(e2).y(), .001f);
		
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
	public void packed_component_set_with_object() throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		Vec2f v1 = new Vec2f(1f, 2f);
		Vec2f v2 = new Vec2f(3f, 4f);
		
		mapper.get(e1).x(1).y(1);
		mapper.get(e2).x(1).y(1);
		
		mapper.get(e1).set(v1);
		mapper.get(e2).set(v2);
		
		String err = String.format("e1=%.0f,%.0f e2=%.0f,%.0f",
			mapper.get(e1).x(), mapper.get(e1).y(),
			mapper.get(e2).x(), mapper.get(e2).y());
		
		assertEquals(err, 1f, mapper.get(e1).x(), .001f);
		assertEquals(err, 2f, mapper.get(e1).y(), .001f);
		assertEquals(err, 3f, mapper.get(e2).x(), .001f);
		assertEquals(err, 4f, mapper.get(e2).y(), .001f);
		
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
