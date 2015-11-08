package com.artemis.injection;

import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.Field;

/**
 * Provides cached information about a class, limiting the need for reflection
 * on repeated access. CachedClass is typically managed by {@link InjectionCache},
 * and can be retrieved with {@link InjectionCache#getCachedClass(Class)}.
 *
 * @author Snorre E. Brekke
 */
public class CachedClass {
	public CachedClass(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * The class that this CachedClass represents.
	 */
	public Class<?> clazz;

	/**
	 * All fields relevant for the class. If the {@link Wire} annotation has injectInherited
	 * set to true, this array will contain ALL declared fields for this class and superclasses.
	 * If injectInherited is false, only the declared fields for {@code clazz} will be cached here.
	 */
	public Field[] allFields;

	/**
	 * The {@link Wire} annotation for this class (at class level).
	 * Only set if {@link #wireType} is set to {@link WireType#WIRE}.
	 */
	public Wire wireAnnotation;

	/**
	 * Cached value of {@link Wire#injectInherited()}
	 */
	public boolean injectInherited;

	/**
	 * Cached value of {@link Wire#failOnNull()}
	 */
	public boolean failOnNull;

	/**
	 * If the class is annotated with {@link Wire}, this will have the type {@link WireType#WIRE}, otherwise it will be
	 * {@link WireType#IGNORED}.
	 */
	public WireType wireType;
}
