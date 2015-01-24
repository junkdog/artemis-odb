package com.artemis;

import java.util.IdentityHashMap;
import java.util.Map;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

class ComponentPool {
	
	private Bag<Pool> pools;

	ComponentPool() {
		pools = new Bag<Pool>();
	}
	
	@SuppressWarnings("unchecked")
	<T extends PooledComponent> T obtain(Class<T> componentClass, ComponentType type)
		throws ReflectionException {
		
		Pool pool = getPool(type.getIndex());
		return (T)((pool.size() > 0) ? pool.obtain() :  ClassReflection.newInstance(componentClass));
	}
	
	void free(PooledComponent c, ComponentType type) {
		free(c, type.getIndex());
	}

	void free(PooledComponent c, int typeIndex) {
		c.reset();
		getPool(typeIndex).free(c);
	}

	private <T extends PooledComponent>Pool getPool(int typeIndex) {
		Pool pool = pools.safeGet(typeIndex);
		if (pool == null) {
			pool = new Pool();
			pools.set(typeIndex, pool);
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
