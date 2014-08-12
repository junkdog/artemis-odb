package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;

public class PackedFieldComponentTest extends PackedWeavingTest {
	
	private PackedFieldComponent packed;
	private ComponentMapper<PackedFieldComponent> mapper;
	private PackedFieldComponent position;

	@Before @Override
	public void setup() {
		super.setup();
		
		packed = e1.createComponent(PackedFieldComponent.class);
		packed = e2.createComponent(PackedFieldComponent.class);
		
		mapper = world.getMapper(PackedFieldComponent.class);
		position = mapper.get(e1);
	}

	@Override
	Class<?> componentType() {
		return PackedFieldComponent.class;
	}
	
	@Override
	ComponentMapper<?> getMapper() {
		return world.getMapper(PackedFieldComponent.class);
	}
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		Field sizeOf = field("$_SIZE_OF");
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(2 * 4, sizeOf.getInt(packed));
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
	
	@Test
	public void increment_and_get_field_value() throws Exception {
		Access access = new Access(position);
		assertEquals(position.toString(), 420, access.incAndGetF(415f), 0.001f);
	}
	
	@Test
	public void multiply_and_get_field_value_2() throws Exception {
		Access access = new Access(position);
		assertEquals(position.toString(), 420, access.mulAndGetF(4, 105), 0.001f);
	}
}
