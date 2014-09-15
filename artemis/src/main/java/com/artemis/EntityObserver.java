package com.artemis;

import com.artemis.utils.ImmutableBag;


/**
 * Used to pass messages to objects that need to be notified about changes to
 * certain entities.
 *
 * @author Arni Arent
 */
public interface EntityObserver {

	/**
	 * Called when an entity the observer is interested in is added.
	 *
	 * @param e
	 *			the added entity
	 */
	void added(Entity e);
	void added(ImmutableBag<Entity> entities);

	/**
	 * Called when an entity the observer is interested in has changed.
	 *
	 * @param e
	 *			the changed entity
	 */
	void changed(Entity e);
	void changed(ImmutableBag<Entity> entities);

	/**
	 * Called when an entity the observer is interested in is deleted.
	 *
	 * @param e
	 *			the deleted entity
	 */
	void deleted(Entity e);
	void deleted(ImmutableBag<Entity> entities);

	/**
	 * Called when an entity the observer is interested in has been
	 * (re)enabled.
	 *
	 * @param e
	 *		the (re)enabled entity
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	void enabled(Entity e);

	/**
	 * Called when an entity the observer is interested in has been disabled.
	 *
	 * @param e
	 *		the disabled entity
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	void disabled(Entity e);

}
