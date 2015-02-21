package com.artemis;

import com.artemis.annotations.Mapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Injects {@link com.artemis.ComponentMapper}, {@link com.artemis.BaseSystem} and {@link com.artemis.Manager} types into systems and
 * managers. Can also inject arbitrary types if registered through {@link com.artemis.WorldConfiguration#register}.
 */
final class Injector {
	private final World world;

	private final Map<Class<?>, Class<?>> systems;
	private final Map<Class<?>, Class<?>> managers;
	private final Map<String, Object> pojos;

	Injector(World world, WorldConfiguration config) {
		this.world = world;

		systems = new IdentityHashMap<Class<?>, Class<?>>();
		managers = new IdentityHashMap<Class<?>, Class<?>>();
		pojos = new HashMap<String, Object>(config.injectables);
	}

	void update() {
		for (BaseSystem es : world.getSystems()) {
			Class<?> origin = es.getClass();
			Class<?> clazz = origin;
			do {
				systems.put(clazz, origin);
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		}

		for (Manager manager : world.getManagers()) {
			Class<?> origin = manager.getClass();
			Class<?> clazz = origin;
			do {
				managers.put(clazz, origin);
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		}

	}

	public void inject(Object target) throws RuntimeException {
		try {
			Class<?> clazz = target.getClass();

			if (ClassReflection.isAnnotationPresent(clazz, Wire.class)) {
				Wire wire = ClassReflection.getAnnotation(clazz, Wire.class);
				if (wire != null) {
					injectValidFields(target, clazz, wire.failOnNull(), wire.injectInherited());
				}
			} else {
				injectAnnotatedFields(target, clazz);
			}
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring", e);
		}
	}

	private void injectValidFields(Object target, Class<?> clazz, boolean failOnNull, boolean injectInherited)
			throws ReflectionException {

		Field[] declaredFields = ClassReflection.getDeclaredFields(clazz);
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			injectField(target, declaredFields[i], failOnNull);
		}

		// should bail earlier, but it's just one more round.
		while (injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
			injectValidFields(target, clazz, failOnNull, injectInherited);
		}
	}

	private void injectAnnotatedFields(Object target, Class<?> clazz)
		throws ReflectionException {

		injectClass(target, clazz);
	}

	@SuppressWarnings("deprecation")
	private void injectClass(Object target, Class<?> clazz) throws ReflectionException {
		Field[] declaredFields = ClassReflection.getDeclaredFields(clazz);
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			Field field = declaredFields[i];
			if (field.isAnnotationPresent(Mapper.class) || field.isAnnotationPresent(Wire.class)) {
				injectField(target, field, field.isAnnotationPresent(Wire.class));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void injectField(Object target, Field field, boolean failOnNotInjected)
		throws ReflectionException {

		field.setAccessible(true);

		Class<?> fieldType;
		try {
			fieldType = field.getType();
		} catch (RuntimeException ignore) {
			// Swallow exception caused by missing typedata on gwt platfString.format("Failed to inject %s into %s: %s not registered with world.")orm.
			// @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for users failing to add systems/components to gwt reflection inclusion config.
			return;
		}

		if (ClassReflection.isAssignableFrom(ComponentMapper.class, fieldType)) {
			ComponentMapper<?> mapper = world.getMapper(field.getElementType(0));
			if (failOnNotInjected && mapper == null)
				throw onFailedInjection("ComponentMapper", field);

			field.set(target, mapper);
		} else if (ClassReflection.isAssignableFrom(BaseSystem.class, fieldType)) {
			BaseSystem system = world.getSystem((Class<BaseSystem>)systems.get(fieldType));
			if (failOnNotInjected && system == null)
				throw onFailedInjection("BaseSystem", field);

			field.set(target, system);
		} else if (ClassReflection.isAssignableFrom(Manager.class, fieldType)) {
			Manager manager = world.getManager((Class<Manager>)managers.get(fieldType));
			if (failOnNotInjected && manager == null)
				throw onFailedInjection("Manager", field);

			field.set(target, manager);
		} else if (ClassReflection.isAssignableFrom(EntityFactory.class, fieldType)) {
			EntityFactory<?> factory = (EntityFactory<?>)world.createFactory(fieldType);
			if (failOnNotInjected && factory == null)
				throw onFailedInjection("EntityFactory", field);

			field.set(target, factory);
		} else if (field.isAnnotationPresent(Wire.class)) {
			final Wire wire = field.getAnnotation(Wire.class);
			String key = wire.name();
			if ("".equals(key))
				key = field.getType().getName();

			if (pojos.containsKey(key))
				field.set(target, pojos.get(key));
		}
	}

	private MundaneWireException onFailedInjection(String typeName, Field failedInjection) {
		String error = new StringBuilder()
			.append("Failed to inject ").append(failedInjection.getType().getName())
			.append(" into ").append(failedInjection.getDeclaringClass().getName()).append(": ")
			.append(typeName).append(" not registered with world.")
			.toString();
		
		return new MundaneWireException(error);
	}
}
