package com.artemis.injection;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntityFactory;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 31/7/2015
 * Time: 17:13 PM
 *
 * @author Snorre E. Brekke
 */
public class InjectionCache {
	private static final Map<Class<?>, CachedClass> classCache = new HashMap<Class<?>, CachedClass>();
	private static final Map<Class<?>, ClassType> fieldClassTypeCache = new HashMap<Class<?>, ClassType>();
	private static final Map<Field, CachedField> namedWireCache = new HashMap<Field, CachedField>();
	private static final Map<Field, Class<?>> genericsCache = new HashMap<Field, Class<?>>();

	private static final Wire DEFAULT_WIRE = new Wire() {

		@Override
		public Class<? extends Annotation> annotationType() {
			return Wire.class;
		}

		@Override
		public boolean injectInherited() {
			return true;
		}

		@Override
		public boolean failOnNull() {
			return true;
		}

		@Override
		public String name() {
			return null;
		}
	};

	public CachedClass getCachedClass(Class<?> clazz) throws ReflectionException {
		CachedClass cachedClass = classCache.get(clazz);
		if (cachedClass == null) {
			cachedClass = new CachedClass(clazz);

			cachedClass.wireType = getWireType(clazz);
			if (cachedClass.wireType == WireType.IGNORED && clazz != Object.class) {
				setWireAnnotation(cachedClass, DEFAULT_WIRE);
			} else if (cachedClass.wireType == WireType.WIRE) {
				setWireAnnotation(cachedClass, ClassReflection.getAnnotation(clazz, Wire.class));
			}

			classCache.put(clazz, cachedClass);
		}
		return cachedClass;
	}

	/**
	 * Set {@code @Wire} annotation value for cached class.
	 */
	private void setWireAnnotation(CachedClass cachedClass, Wire wireAnnotation) {
		cachedClass.wireType = WireType.WIRE;
		cachedClass.wireAnnotation = wireAnnotation;
		cachedClass.failOnNull = wireAnnotation.failOnNull();
		cachedClass.injectInherited = wireAnnotation.injectInherited();
	}

	/**
	 * Determine desired wiring on class by annotation.
	 * Convention is {@code Wire(injectInherited=true)}
	 */
	private WireType getWireType(Class<?> clazz) {
		return ClassReflection.isAnnotationPresent(clazz, Wire.class) ?
				WireType.WIRE : (ClassReflection.isAnnotationPresent(clazz, SkipWire.class) ?
				WireType.SKIPWIRE :
				WireType.IGNORED);
	}


	public CachedField getCachedField(Field field) {
		CachedField cachedField = namedWireCache.get(field);
		if (cachedField == null) {
			if (field.isAnnotationPresent(Wire.class)) {
				final Wire wire = field.getAnnotation(Wire.class);
				cachedField = new CachedField(field, WireType.WIRE, wire.name(), wire.failOnNull());
			} else if (field.isAnnotationPresent(SkipWire.class)) {
				cachedField = new CachedField(field, WireType.SKIPWIRE, null, false);
			} else {
				cachedField = new CachedField(field, WireType.IGNORED, null, false);
			}
			namedWireCache.put(field, cachedField);
		}
		return cachedField;
	}


	public ClassType getFieldClassType(Class<?> fieldType) {
		ClassType injectionType = fieldClassTypeCache.get(fieldType);
		if (injectionType == null) {
			if (ClassReflection.isAssignableFrom(ComponentMapper.class, fieldType)) {
				injectionType = ClassType.MAPPER;
			} else if (ClassReflection.isAssignableFrom(BaseSystem.class, fieldType)) {
				injectionType = ClassType.SYSTEM;
			} else if (ClassReflection.isAssignableFrom(EntityFactory.class, fieldType)) {
				injectionType = ClassType.FACTORY;
			} else {
				injectionType = ClassType.CUSTOM;
			}
			fieldClassTypeCache.put(fieldType, injectionType);
		}
		return injectionType;
	}

	public Class<?> getGenericType(Field field) {
		Class<?> genericsType = genericsCache.get(field);
		if (genericsType == null) {
			genericsType = field.getElementType(0);
			genericsCache.put(field, genericsType);
		}
		return genericsType;
	}


}
