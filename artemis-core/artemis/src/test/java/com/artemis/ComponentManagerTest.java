package com.artemis;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Before;

public class ComponentManagerTest {

	private World world;

	@Before
	public void init() {
		
		world = new World();

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

	private static Field field(String f) throws NoSuchFieldException {
		Field field = ComponentTypeFactory.class.getDeclaredField(f);
		field.setAccessible(true);
		return field;
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
