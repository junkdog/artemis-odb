package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;

public class PackedComponentWeavingTest {
	
	private World world;
	private Entity e1, e2;
	private TransPackedFloat packed;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
		
		e1 = world.createEntity();
		packed = e1.createComponent(TransPackedFloat.class);
		e1.addToWorld();
		
		e2 = world.createEntity();
		packed = e2.createComponent(TransPackedFloat.class);
		e2.addToWorld();
	}
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		Field sizeOf = field("$_SIZE_OF");
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(2, sizeOf.getInt(packed));
	}
	
	@Test @SuppressWarnings("static-method")
	public void packed_component_has_offset() throws Exception {
		Field offset = field("$offset");
		
		assertEquals(PRIVATE, offset.getModifiers());
		assertEquals(int.class, offset.getType());
	}
	
	@Test
	public void packed_component_updates_offset() throws Exception {
		assertNotEquals(getOffset(e1), getOffset(e2));
	}
	
	private int getOffset(Entity e) throws Exception {
		ComponentMapper<TransPackedFloat> mapper = world.getMapper(TransPackedFloat.class);
		return field("$offset").getInt(mapper.get(e));
	}
	
	private static Field field(String name) throws NoSuchFieldException {
		Field f = TransPackedFloat.class.getDeclaredField(name);
		assertNotNull(f);
		f.setAccessible(true);
		return f;
	}
}
