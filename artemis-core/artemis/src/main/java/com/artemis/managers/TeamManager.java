package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;


/**
 * Use this class together with PlayerManager.
 * <p>
 * You may sometimes want to create teams in your game, so that some players
 * are team mates.
 * </p><p>
 * A player can only belong to a single team.
 * </p>
 * 
 * @author Arni Arent
 */
public class TeamManager extends Manager {

	/** Teams mapped to their players. */
	private final Map<String, Bag<String>> playersByTeam;
	/** Players mapped to their teams. */
	private final Map<String, String> teamByPlayer;


	/**
	 * Creates a new TeamManager instance.
	 */
	public TeamManager() {
		playersByTeam = new HashMap<String, Bag<String>>();
		teamByPlayer = new HashMap<String, String>();
	}



	@Override
	protected void initialize() {
	}

	/**
	 * The the name of the team the given player is in.
	 *
	 * @param player
	 *			the player
	 *
	 * @return the player's team
	 */
	public String getTeam(String player) {
		return teamByPlayer.get(player);
	}

	/**
	 * Set the player's team.
	 * <p>
	 * Each player can only be in one team at a time.
	 * </p>
	 *
	 * @param player
	 *			the player
	 * @param team
	 *			the team to put the player in
	 */
	public void setTeam(String player, String team) {
		removeFromTeam(player);
		
		teamByPlayer.put(player, team);
		
		Bag<String> players = playersByTeam.get(team);
		if(players == null) {
			players = new Bag<String>();
			playersByTeam.put(team, players);
		}
		players.add(player);
	}

	/**
	 * Get all players on a team.
	 *
	 * @param team
	 *			the team
	 *
	 * @return all players on the team in a bag
	 */
	public ImmutableBag<String> getPlayers(String team) {
		return playersByTeam.get(team);
	}

	/**
	 * Remove a player from his team.
	 *
	 * @param player
	 *			the player to remove
	 */
	public void removeFromTeam(String player) {
		String team = teamByPlayer.remove(player);
		if(team != null) {
			Bag<String> players = playersByTeam.get(team);
			if(players != null) {
				players.remove(player);
			}
		}
	}

}
