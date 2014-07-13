package com.artemis;

import java.util.IdentityHashMap;

/**
 * Used to generate a unique bit for each system.
 * <p>
 * Only used internally in EntitySystem.
 * </p>
 */
final class SystemIndexManager {

	/** Amount of EntitySystem indices. */
	private int INDEX = 0;
	
	/**
	 * Contains the class types of all created systems.
	 * <p>
	 * Only one system per class is permitted in the world.
	 * </p>
	 */
	private final IdentityHashMap<Class<? extends EntitySystem>, Integer> indices
			= new IdentityHashMap<Class<? extends EntitySystem>, Integer>();

	/**
	 * Called by the EntitySystem constructor.
	 * Will give the new EntitySystem the next index, and store the systems
	 * class as an (Index, Class) entry.
	 *
	 * @param es
	 *			the systems class type
	 *
	 * @return the systems index
	 */
	int getIndexFor(Class<? extends EntitySystem> es) {
		Integer index = indices.get(es);
		if(index == null) {
			index = INDEX++;
			indices.put(es, index);
		}
		return index;
	}
}