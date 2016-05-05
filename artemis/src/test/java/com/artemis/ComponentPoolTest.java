package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.artemis.component.PooledString;
import org.junit.Test;

public class ComponentPoolTest
{
	@SuppressWarnings("static-method")
	@Test
	public void reuse_pooled_components() throws Exception
	{

		ComponentType type = new ComponentType(SimplePooled.class, 0);
		ComponentPool<SimplePooled> pool = new ComponentPool<SimplePooled>(SimplePooled.class);

		SimplePooled c1 = pool.obtain();
		SimplePooled c2 = pool.obtain();
		pool.free(c1);
		SimplePooled c1b = pool.obtain();
		
		assertTrue(c1 != c2);
		assertTrue(c1 == c1b);
	}

	@Test
	public void test_pooled_components_reused_and_freed() {
		assertEquals(PooledComponent.class, PooledString.class.getSuperclass());

		World world = new World();

		Entity e = world.createEntity();
		PooledString ps = e.edit().create(PooledString.class);
		ps.s = "i'm instanced";

		world.process();
		e.deleteFromWorld();
		world.process();

		Entity e2 = world.createEntity();
		PooledString ps2 = e2.edit().create(PooledString.class);

		assertTrue(ps == ps2);
		assertNull(ps.s);
	}
	
	public static final class SimplePooled extends PooledComponent {
		
		@Override
		public void reset() {}
	}
}
