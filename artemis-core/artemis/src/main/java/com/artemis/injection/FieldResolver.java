package com.artemis.injection;

import com.artemis.World;
import com.artemis.utils.reflect.Field;

/**
 * API used by {@link FieldHandler} to resolve field values in classes eligible for injection.
 *
 * @author Snorre E. Brekke
 */
public interface FieldResolver {

	/**
	 * Called after Wo
	 *
	 * @param world
	 */
	void initialize(World world);

	/**
	 * @param target object which should have dependencies injected.
	 */
	Object resolve(Object target, Class<?> fieldType, Field field);
}
