package com.artemis.injection;

import java.util.Map;

/**
 * Field resolver for manually registered objects, for injection by type or name.
 *
 * @see com.artemis.WorldConfiguration#register
 * @author Daan van Yperen
 */
public interface PojoFieldResolver extends FieldResolver {

	/**
	 * Set manaully registered objects.
	 * @param pojos Map of manually registered objects.
	 */
	void setPojos(Map<String, Object> pojos);
}
