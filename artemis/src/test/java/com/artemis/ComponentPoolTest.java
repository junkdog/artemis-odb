package com.artemis;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComponentPoolTest
{
	@SuppressWarnings("static-method")
	@Test
	public void reuse_pooled_components() throws Exception
	{
		ComponentPool pool = new ComponentPool();

		ComponentType type = new ComponentType(SimplePooled.class, 0);

		SimplePooled c1 = pool.obtain(SimplePooled.class, type);
		SimplePooled c2 = pool.obtain(SimplePooled.class, type);
		pool.free(c1, type);
		SimplePooled c1b = pool.obtain(SimplePooled.class, type);
		
		assertTrue(c1 != c2);
		assertTrue(c1 == c1b);
	}
	
	public static final class SimplePooled extends PooledComponent {
		
		@Override
		public void reset() {}
	}
}
