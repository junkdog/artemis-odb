package com.artemis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.artemis.component.Packed;

public class PackedComponentTest
{
	private World world;
	private ComponentMapper<Packed> packedMapper;

	@Before
	public void init() {
		world = new World();
		packedMapper = world.getMapper(Packed.class);
	}
	
	@Test
	public void packed_components_are_known_to_mapper() {

		List<Integer> packed = new ArrayList<Integer>();
		packed.add(createEntity(Packed.class));
		packed.add(createEntity(Packed.class));
		packed.add(createEntity(Packed.class));
		int notPresent = createEntity();
		packed.add(createEntity(Packed.class));
		packed.add(createEntity(Packed.class));
		
		for (int e : packed) {
			 assertTrue(packedMapper.has(e));
		}
		 assertFalse(packedMapper.has(notPresent));
		 
		 EntityHelper.edit(world, packed.get(1)).remove(Packed.class);
		 for (int i = 0; packed.size() > i; i++) {
			 if (i != 1) assertTrue(packedMapper.has(packed.get(i)));
		 }
		 assertFalse(packedMapper.has(packed.get(1)));
	}
	
	@Test
	public void packed_component_mappers_return_new_instance_on_request() {

		int e1 = createEntity(Packed.class);
		int e2 = createEntity(Packed.class);
		Packed packed1 = packedMapper.get(e1, true);
		Packed packed2 = packedMapper.get(e2, true);

		assertNotEquals(packed1.entityId, packed2.entityId);
	}
	
	@SuppressWarnings("unchecked")
	private int createEntity(Class<?>... components) {
		int e = world.createEntity();
		for (Class<?> c : components) {
			EntityHelper.edit(world, e).create((Class<Component>)c);
		}
		
		return e;
	}
}
