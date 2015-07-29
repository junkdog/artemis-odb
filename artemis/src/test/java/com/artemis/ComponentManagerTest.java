package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

public class ComponentManagerTest {

	private World world;

	@Before
	public void init() {
		
		world = new World();
		world.initialize();

		try {
			Field field = field("componentTypeCount");
			field.setInt(world.getComponentManager().typeFactory, 0xffff);
		} catch (NoSuchFieldException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void ensure_basic_components_dont_throw_aioob() throws Exception {
		world.getMapper(Basic.class);
		ComponentTypeFactory typeFactory = world.getComponentManager().typeFactory;
		assertTrue(0xffff <= field("componentTypeCount").getInt(typeFactory));
		assertEquals(0xffff, typeFactory.getTypeFor(Basic.class).getIndex());
	}
	
	@Test
	public void ensure_pooled_components_dont_throw_aioob() throws Exception {
		world.getMapper(Pooled.class);
		ComponentTypeFactory typeFactory = world.getComponentManager().typeFactory;
		assertTrue(0xffff <= field("componentTypeCount").getInt(typeFactory));
		assertEquals(0xffff, typeFactory.getIndexFor(Pooled.class));
	}
	
	@Test
	public void ensure_packed_components_dont_throw_aioob() throws Exception {
		world.getMapper(Packed.class);
		ComponentTypeFactory typeFactory = world.getComponentManager().typeFactory;
		assertTrue(0xffff <= field("componentTypeCount").getInt(typeFactory));
		assertEquals(0xffff, typeFactory.getIndexFor(Packed.class));
	}
	
	@Test
	public void instantiate_packed_empty_constructor() {
		Entity e = world.createEntity();
		assertNotNull(e.edit().create(Packed.class));
	}
	
	@Test
	public void instantiate_packed_world_constructor() {
		Entity e = world.createEntity();
		assertNotNull(e.edit().create(PackedWorld.class));
	}
	
	private static Field field(String f) throws NoSuchFieldException {
		Field field = ComponentTypeFactory.class.getDeclaredField(f);
		field.setAccessible(true);
		return field;
	}
	
	public static class Packed extends PackedComponent {
		public int entityId;
		
		@Override
		protected void forEntity(int entityId) {
			this.entityId = entityId;
		}
		
		@Override
		protected void reset() {}

		@Override
		protected void ensureCapacity(int id) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class PackedWorld extends PackedComponent {
		public int entityId;

		public PackedWorld(World world) {
			assertNotNull(world);
		}
		
		@Override
		protected void forEntity(int entityId) {
			this.entityId = entityId;
		}

		@Override
		protected void reset() {}

		@Override
		protected void ensureCapacity(int id) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class Pooled extends PooledComponent {
		@Override
		public void reset() {}
	}
	
	private class Basic extends Component {
		@SuppressWarnings("unused")
		public String text;
	}
}
