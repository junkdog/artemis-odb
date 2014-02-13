package com.artemis;

import java.util.IdentityHashMap;
import java.util.Map;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

class ComponentPool {
	
	private final Map<Class<? extends PooledComponent>, Pool> pools;
	
	ComponentPool() {
		pools = new IdentityHashMap<Class<? extends PooledComponent>, ComponentPool.Pool>();
	}
	
	@SuppressWarnings("unchecked")
	<T extends PooledComponent> T obtain(Class<T> componentClass)
		throws ReflectionException {
		
		Pool pool = getPool(componentClass);
		return (T)((pool.size() > 0) ? pool.obtain() :  ClassReflection.newInstance(componentClass));
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
