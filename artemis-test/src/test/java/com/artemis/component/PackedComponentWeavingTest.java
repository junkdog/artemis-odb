package com.artemis.component;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;

public class PackedComponentWeavingTest {
	
	private World world;
	private TransPackedFloat packed;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
		
		Entity e = world.createEntity();
		packed = e.createComponent(TransPackedFloat.class);
		e.addToWorld();
	}
	
	@Test
	public void packed_component_has_sizeof() throws Exception {
		String fields = Arrays.toString(TransPackedFloat.class.getDeclaredFields());
		System.out.println("FOUND FIELDS: " + fields);
		Field sizeOf = TransPackedFloat.class.getDeclaredField("$_SIZE_OF");
		assertNotNull(sizeOf);
		sizeOf.setAccessible(true);
		
		assertEquals(PRIVATE | STATIC | FINAL, sizeOf.getModifiers());
		assertEquals(int.class, sizeOf.getType());
		assertEquals(2, sizeOf.getInt(packed));
	}
}
