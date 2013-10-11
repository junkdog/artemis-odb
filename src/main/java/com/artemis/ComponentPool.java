package com.artemis;

import java.util.HashMap;
import java.util.Map;

import com.artemis.utils.Bag;

class ComponentPool {
	
	private final Map<Class<? extends PooledComponent>, Pool> pools;
	
	ComponentPool() {
		pools = new HashMap<Class<? extends PooledComponent>, ComponentPool.Pool>();
	}
	
	@SuppressWarnings("unchecked")
	<T extends PooledComponent> T obtain(Class<T> componentClass)
		throws InstantiationException, IllegalAccessException {
		
		Pool pool = getPool(componentClass);
		return (T)((pool.size() > 0) ? pool.obtain() : componentClass.newInstance());
	}
	
	void free(PooledComponent c) {
		c.reset();
		getPool(c.getClass()).free(c);
	}
	
	private <T extends PooledComponent>Pool getPool(Class<T> componentClass)
	{
		Pool pool = pools.get(componentClass);
		if (pool == null) {
			pool = new Pool();
			pools.put(componentClass, pool);
		}
		return pool;
	}
	
	private static class Pool {
		private final Bag<PooledComponent> cache = new Bag<PooledComponent>();
		
		@SuppressWarnings("unchecked")
		<T extends PooledComponent> T obtain() {
			return (T)cache.removeLast();
		}
		
		int size() {
			return cache.size();
		}
		
		void free(PooledComponent component) {
			cache.add(component);
		}
	}
}
