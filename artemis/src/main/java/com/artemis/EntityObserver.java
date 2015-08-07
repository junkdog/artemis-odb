package com.artemis;

import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;


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
	 * @param entityId
	 *			the added entities
	 */
	void added(int entityId);
	void added(IntBag entities);

	/**
	 * Called when an entity the observer is interested in has changed.
	 *
	 * @param entityId
	 *			the changed entities
	 */
	void changed(int entityId);
	void changed(IntBag entities);

	/**
	 * Called when an entity the observer is interested in is deleted.
	 *
	 * @param entityId
	 *			the deleted entities
	 */
	void deleted(int entityId);
	void deleted(IntBag entities);

	/**
	 * Called when an entity the observer is interested in has been
	 * (re)enabled.
	 *
	 * @param entityId
	 *		the (re)enabled entity
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	void enabled(int entityId);

	/**
	 * Called when an entity the observer is interested in has been disabled.
	 *
	 * @param entityId
	 *		the disabled entity
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	void disabled(int entityId);

}
