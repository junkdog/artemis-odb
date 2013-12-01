package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;

public class FloatPackedWeaving2Test extends PackedWeavingTest {
	
	private Position packed;
	private ComponentMapper<Position> mapper;
	private Position position;

	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.createComponent(Position.class);
		packed = e2.createComponent(Position.class);
		
		mapper = world.getMapper(Position.class);
		position = mapper.get(e1);
	}
	
	@Override
	int fieldCount() {
		return 2;
	}

	@Override
	Class<?> fieldType() {
		return float[].class;
	}
	
	@Override
	Class<?> componentType() {
		return Position.class;
	}
	
	@Override
	ComponentMapper<?> getMapper() {
		return world.getMapper(Position.class);
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
		assertEquals(64 * fieldCount(), ((float[])data.get(null)).length);
		
		Method grow = method("$grow");
		grow.invoke(packed);
		assertEquals(64 * fieldCount() * 2, ((float[])data.get(null)).length);
	}
	
	@Test
	public void has_methods_for_fields() throws Exception {
		method("x");
		method("y");
		method("x", float.class);
		method("y", float.class);
	}
	
	@Test
	public void set_and_get_field_value() throws Exception {
		Access access = new Access(position);
		assertEquals(position.toString(), 420, access.setAndGetF(420f), 0.001f);
	}
	
//	@Test 
//	public void packed_component_replaces_field_access_with_backing_array() throws Exception {
//		ComponentMapper<Position> mapper = world.getMapper(Position.class);
//		
//		mapper.get(e1).x = 1;
//		mapper.get(e1).y = 2;
//		mapper.get(e2).x = 3;
//		mapper.get(e2).y = 4;
//		
//		assertEquals(1f, mapper.get(e1).x, .001f);
//		assertEquals(2f, mapper.get(e1).y, .001f);
//		assertEquals(3f, mapper.get(e2).x, .001f);
//		assertEquals(4f, mapper.get(e2).y, .001f);
//		
//		try {
//			Position.class.getDeclaredField("x");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//		try {
//			Position.class.getDeclaredField("y");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//	}
	
//	@Test
//	public void packed_component_add_to_value() throws Exception {
//		ComponentMapper<Position> mapper = world.getMapper(Position.class);
//		mapper.get(e1).x = 1;
//		mapper.get(e1).y = 2;
//		mapper.get(e2).x = 3;
//		mapper.get(e2).y = 4;
//		
//		mapper.get(e1).x += 4;
//		mapper.get(e2).x += 8;
//		
//		assertEquals(5f, mapper.get(e1).x, .001f);
//		assertEquals(2f, mapper.get(e1).y, .001f);
//		assertEquals(11f, mapper.get(e2).x, .001f);
//		assertEquals(4f, mapper.get(e2).y, .001f);
//		
//		try {
//			Position.class.getDeclaredField("x");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//		try {
//			Position.class.getDeclaredField("y");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//	}
//	
//	@Test
//	public void packed_component_add_with_object() throws Exception {
//		ComponentMapper<Position> mapper = world.getMapper(Position.class);
//		Vec2f v1 = new Vec2f(1f, 2f);
//		Vec2f v2 = new Vec2f(3f, 4f);
//		
//		mapper.get(e1).x = 1;
//		mapper.get(e1).y = 1;
//		mapper.get(e2).x = 1;
//		mapper.get(e2).y = 1;
//		
//		mapper.get(e1).add(v1);
//		mapper.get(e2).add(v2);
//		
//		String err = String.format("e1=%.0f,%.0f e2=%.0f,%.0f",
//			mapper.get(e1).x, mapper.get(e1).y,
//			mapper.get(e2).x, mapper.get(e2).y);
//		
//		assertEquals(err, 2f, mapper.get(e1).x, .001f);
//		assertEquals(err, 3f, mapper.get(e1).y, .001f);
//		assertEquals(err, 4f, mapper.get(e2).x, .001f);
//		assertEquals(err, 5f, mapper.get(e2).y, .001f);
//		
//		try {
//			Position.class.getDeclaredField("x");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//		try {
//			Position.class.getDeclaredField("y");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//	}
//	
//	@Test
//	public void packed_component_set_with_object() throws Exception {
//		ComponentMapper<Position> mapper = world.getMapper(Position.class);
//		Vec2f v1 = new Vec2f(1f, 2f);
//		Vec2f v2 = new Vec2f(3f, 4f);
//		
//		mapper.get(e1).x = 1;
//		mapper.get(e1).y = 1;
//		mapper.get(e2).x = 1;
//		mapper.get(e2).y = 1;
//		
//		mapper.get(e1).set(v1);
//		mapper.get(e2).set(v2);
//		
//		String err = String.format("e1=%.0f,%.0f e2=%.0f,%.0f",
//			mapper.get(e1).x, mapper.get(e1).y,
//			mapper.get(e2).x, mapper.get(e2).y);
//		
//		assertEquals(err, 1f, mapper.get(e1).x, .001f);
//		assertEquals(err, 2f, mapper.get(e1).y, .001f);
//		assertEquals(err, 3f, mapper.get(e2).x, .001f);
//		assertEquals(err, 4f, mapper.get(e2).y, .001f);
//		
//		try {
//			Position.class.getDeclaredField("x");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//		try {
//			Position.class.getDeclaredField("y");
//			fail("Failed to remove field from component");
//		} catch (Exception e) { /* expected */ }
//	}
}
