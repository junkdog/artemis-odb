package com.artemis;


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

	/**
	 * Called when an entity the observer is interested in has changed.
	 *
	 * @param e
	 *			the changed entity
	 */
	void changed(Entity e);

	/**
	 * Called when an entity the observer is interested in is deleted.
	 *
	 * @param e
	 *			the deleted entity
	 */
	void deleted(Entity e);

	/**
	 * Called when an entity the observer is interested in has been
	 * (re)enabled.
	 *
	 * @param e
	 *		the (re)enabled entity
	 */
	void enabled(Entity e);

	/**
	 * Called when an entity the observer is interested in has been disabled.
	 *
	 * @param e
	 *		the disabled entity
	 */
	void disabled(Entity e);

}
