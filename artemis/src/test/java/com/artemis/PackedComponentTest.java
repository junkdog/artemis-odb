package com.artemis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PackedComponentTest
{
	private World world;

	@Before
	public void init() {
		world = new World();
	}
	
	@Test
	public void packed_components_are_known_to_mapper() {
		world.initialize();
		
		ComponentMapper<Packed> mapper = world.getMapper(Packed.class);
		
		List<Entity> packed = new ArrayList<Entity>();
		 packed.add(createEntity(Packed.class));
		 packed.add(createEntity(Packed.class));
		 packed.add(createEntity(Packed.class));
		 Entity notPresent = createEntity();
		 packed.add(createEntity(Packed.class));
		 packed.add(createEntity(Packed.class));
		
		 for (Entity e : packed) {
			 assertTrue(mapper.has(e));
		 }
		 assertFalse(mapper.has(notPresent));
		 
		 packed.get(1).removeComponent(Packed.class);
		 for (int i = 0; packed.size() > i; i++) {
			 if (i != 1) assertTrue(mapper.has(packed.get(i)));
		 }
		 assertFalse(mapper.has(packed.get(1)));
	}
	
	@SuppressWarnings("unchecked")
	private Entity createEntity(Class<?>... components) {
		Entity e = world.createEntity();
		for (Class<?> c : components) {
			e.createComponent((Class<Component>)c);
		}
		e.addToWorld();
		return e;
	}
	
	public static class Packed extends PackedComponent {
		
		@Override
		protected PackedComponent forEntity(Entity e)
		{
			return this;
		}

		@Override
		protected void reset() {}
	}
}
