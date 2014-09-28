package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

class EntityTemplateFactory {

	private final World world;
	
	private Bag<EntityFactory<?>> factories = new Bag<EntityFactory<?>>();

	public EntityTemplateFactory(World world) {
		this.world = world;		
	}
	
	<T extends EntityFactory<T>> T resolve(Class<T> factory) {
		if (!factory.isInterface())
			throw new RuntimeException("Expected interface for type: " + factory);

		String impl = factory.getCanonicalName() + "Impl";
		try {
			Class<?> implClass = ClassReflection.forName(impl);
			@SuppressWarnings("unchecked")
			T instance = (T)ClassReflection.newInstance(implClass);
			return instance;
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
