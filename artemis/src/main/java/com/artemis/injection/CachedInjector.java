package com.artemis.injection;

import com.artemis.*;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * By default, injects {@link ComponentMapper}, {@link BaseSystem} and {@link Manager} types into systems and
 * managers. Can also inject arbitrary types if registered through {@link WorldConfiguration#register}.
 *
 * Caches all type-information.
 *
 * <p>
 * For greater control over the dependency-resolution, provide a {@link FieldHandler} to {@link #setFieldHandler(FieldHandler)},
 * which will be used to resolve dependency values instead.
 * </p>
 *
 * @author Arni Arent
 * @author Snorre E. Brekke
 * @see com.artemis.injection.FieldHandler
 */
public final class CachedInjector implements Injector {
	private InjectionCache cache = new InjectionCache();
	private FieldHandler fieldHandler;
	private Map<String, Object> injectables;

	@Override
	public Injector setFieldHandler(FieldHandler fieldHandler) {
		this.fieldHandler = fieldHandler;
		return this;
	}

	@Override
	public <T> T getRegistered(String id) {
		return (T) injectables.get(id);
	}

	@Override
	public <T> T getRegistered(Class<T> id) {
		return getRegistered(id.getName());
	}

	@Override
	public void initialize(World world, Map<String, Object> injectables) {
		this.injectables = injectables;
		if (fieldHandler == null) {
			fieldHandler = new FieldHandler(cache);
		}

		fieldHandler.initialize(world, injectables);
	}

	@Override
	public boolean isInjectable(Object target) {
		try {
			CachedClass cachedClass = cache.getCachedClass(target.getClass());
			return cachedClass.wireType == WireType.WIRE;
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring", e);
		}
	}

	@Override
	public void inject(Object target) throws RuntimeException {
		try {
			Class<?> clazz = target.getClass();
			CachedClass cachedClass = cache.getCachedClass(clazz);

			if (cachedClass.wireType == WireType.WIRE) {
				injectValidFields(target, cachedClass);
			} else {
				injectAnnotatedFields(target, cachedClass);
			}
		} catch (RuntimeException e ) {
			throw new MundaneWireException("Error while wiring " + target.getClass().getName(), e);
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring " + target.getClass().getName(), e);
		}
	}

	private void injectValidFields(Object target, CachedClass cachedClass)
			throws ReflectionException {
		Field[] declaredFields = getAllInjectableFields(cachedClass);
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			injectField(target, declaredFields[i], cachedClass.failOnNull);
		}
	}

	private Field[] getAllInjectableFields(CachedClass cachedClass) {
		Field[] declaredFields = cachedClass.allFields;
		if (declaredFields == null) {
			List<Field> fieldList = new ArrayList<Field>();
			Class<?> clazz = cachedClass.clazz;
			collectDeclaredInjectableFields(fieldList, clazz);

			while (cachedClass.injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
				collectDeclaredInjectableFields(fieldList, clazz);
			}
			cachedClass.allFields = declaredFields = fieldList.toArray(new Field[fieldList.size()]);
		}
		return declaredFields;
	}

	private void collectDeclaredInjectableFields(List<Field> fieldList, Class<?> clazz) {
		try {
			if (cache.getCachedClass(clazz).wireType != WireType.SKIPWIRE) {
				Field[] classFields = ClassReflection.getDeclaredFields(clazz);
				for (int i = 0; i < classFields.length; i++) {
					if (isWireable(classFields[i])) {
						fieldList.add(classFields[i]);
					}
				}
			}
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring", e);
		}
	}

	private boolean isWireable(Field field) {
		return cache.getCachedField(field).wireType != WireType.SKIPWIRE;
	}

	private void injectAnnotatedFields(Object target, CachedClass cachedClass)
			throws ReflectionException {
		injectClass(target, cachedClass);
	}

	@SuppressWarnings("deprecation")
	private void injectClass(Object target, CachedClass cachedClass) throws ReflectionException {
		Field[] declaredFields = getAllInjectableFields(cachedClass);
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			Field field = declaredFields[i];
			CachedField cachedField = cache.getCachedField(field);
			if (cachedField.wireType != WireType.IGNORED) {
				injectField(target, field, cachedField.wireType == WireType.WIRE);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void injectField(Object target, Field field, boolean failOnNotInjected)
			throws ReflectionException {
		Class<?> fieldType;
		try {
			fieldType = field.getType();
		} catch (RuntimeException ignore) {
			// Swallow exception caused by missing typedata on gwt platfString.format("Failed to inject %s into %s:
			// %s not registered with world.")orm.
			// @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for
			// users failing to add systems/components to gwt reflection inclusion config.
			return;
		}

		Object resolve = fieldHandler.resolve(fieldType, field);
		if (resolve != null) {
			setField(target, field, resolve);
		}

		if (resolve == null && failOnNotInjected && cache.getFieldClassType(fieldType) != ClassType.CUSTOM) {
			throw onFailedInjection(fieldType.getSimpleName(), field);
		}
	}

	private void setField(Object target, Field field, Object fieldValue) throws ReflectionException {
		field.setAccessible(true);
		field.set(target, fieldValue);
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
