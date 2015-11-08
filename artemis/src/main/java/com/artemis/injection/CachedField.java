package com.artemis.injection;

import com.artemis.utils.reflect.Field;

/**
 * Provides cached information about a class-field, limiting the need for reflection
 * on repeated access.
 * This class only caches the state related to the {@link com.artemis.annotations.Wire} annotation of the field.
 * <p>
 * CachedField is typically managed by {@link InjectionCache},
 * and can be retrieved with {@link InjectionCache#getCachedField(Field)}.
 * </p>
 *
 * @author Snorre E. Brekke
 */
public class CachedField {
	public CachedField(Field field, WireType wireType, String name, boolean failOnNull) {
		this.field = field;
		this.wireType = wireType;
		this.name = name;
		this.failOnNull = failOnNull;
	}

	/**
	 * The field this class represents.
	 */
	public final Field field;

	/**
	 * {@link WireType#WIRE} if the field is annotated with {@link com.artemis.annotations.Wire},
	 * {@link WireType#SKIPWIRE} if the field is annotated with {@link com.artemis.annotations.SkipWire),
	 * {@link WireType#IGNORED} otherwise.
	 */
	public final WireType wireType;

	/**
	 * If the field is annotated with {@link com.artemis.annotations.Wire}, this will contain the cached value of
	 * {@link com.artemis.annotations.Wire#name()}.
	 */
	public final String name;


	public final boolean failOnNull;
}
