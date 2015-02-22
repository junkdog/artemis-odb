package com.artemis;

import com.artemis.utils.ImmutableBag;


/**
 * EntityObservers are invoked when an entity is created, changes composition id
 * or is deleted. At the moment, only {@Manager | Managers} implement this interface.
 *
 * <p>The {@link com.artemis.EntitySubscription.SubscriptionListener} while similar,
 * should not be confused with this interface, as only report entities inserted or
 * removed when matched against {@link com.artemis.EntitySubscription}</p>
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
