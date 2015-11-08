package com.artemis.injection;

import com.artemis.World;

/**
 * {@link FieldResolver} implementing this interface will have the {@link #setCache(InjectionCache)}
 * method called during {@link FieldHandler#initialize(World)}, prior to {@link FieldResolver#initialize(World)}
 * being called.
 *
 * @author Snorre E. Brekke
 */
public interface UseInjectionCache {
	/**
	 * @param cache used by the {@link FieldHandler}
	 */
	void setCache(InjectionCache cache);
}
