package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

class ComponentPool {
	
	private Bag<Pool> pools;

	ComponentPool() {
		pools = new Bag(Pool.class);
	}
	
	@SuppressWarnings("unchecked")
	<T extends PooledComponent> T obtain(ComponentType type) {
		
		Pool pool = getPool(type.getIndex());
		try {
			return (T)((pool.size() > 0) ? pool.obtain() :  ClassReflection.newInstance(type.getType()));
		} catch (ReflectionException e) {
			throw new InvalidComponentException(type.getType(), e.getMessage(), e);
		}
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
		private final Bag<PooledComponent> cache = new Bag(PooledComponent.class);
		
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
