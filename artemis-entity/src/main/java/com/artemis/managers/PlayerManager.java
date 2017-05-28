package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;


/**
 * You may sometimes want to specify to which player an entity belongs to.
 * <p>
 * An entity can only belong to a single player at a time.
 * </p>
 *
 * @author Arni Arent
 */
public class PlayerManager<T extends Entity> extends Manager<T> {

	/** All players mapped to entities as key. */
	private final Map<T, String> playerByEntity;
	/** All entities that are mapped to a player, with the player as key. */
	private final Map<String, Bag<T>> entitiesByPlayer;

	/**
	 * Creates a new PlayerManager instance.
	 */
	public PlayerManager() {
		playerByEntity = new HashMap<T, String>();
		entitiesByPlayer = new HashMap<String, Bag<T>>();
	}


	/**
	 * Associate the entity with the specified player.
	 * <p>
	 * Each entity may only be assoctiated with one player at a time.
	 * </p>
	 *
	 * @param e
	 *			the entity to associate
	 * @param player
	 *			the player to associtate to the entity with
	 */
	public void setPlayer(T e, String player) {
		playerByEntity.put(e, player);
		Bag<T> entities = entitiesByPlayer.get(player);
		if(entities == null) {
			entities = new Bag<T>();
			entitiesByPlayer.put(player, entities);
		}
		entities.add(e);
	}

	public void setPlayer(int id, String player) {
		setPlayer(world.getEntity(id), player);
	}

		/**
         * Get all entities belonging to a player.
         *
         * @param player
         *			the player
         *
         * @return a bag containing all entities belonging to the player
         */
	public ImmutableBag<T> getEntitiesOfPlayer(String player) {
		Bag<T> entities = entitiesByPlayer.get(player);
		if(entities == null) {
			entities = new Bag<T>();
		}
		return entities;
	}

	/**
	 * Remove the association of an entity with a player.
	 *
	 * @param e
	 *			the entity to remove
	 */
	public void removeFromPlayer(T e) {
		String player = playerByEntity.get(e);
		if(player != null) {
			Bag<T> entities = entitiesByPlayer.get(player);
			if(entities != null) {
				entities.remove(e);
			}
		}
	}

	/**
	 * Get the player an entity is associated with.
	 *
	 * @param e
	 *			the entity to get the player for
	 *
	 * @return the player
	 */
	public String getPlayer(T e) {
		return playerByEntity.get(e);
	}

	/**
	 * Deleted entities are removed from their player.
	 *
	 * @param e
	 *			the deleted entity
	 */
	@Override
	public void deleted(T e) {
		removeFromPlayer(e);
	}
}
