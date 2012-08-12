package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;


/**
 * Use this class together with PlayerManager.
 * 
 * You may sometimes want to create teams in your game, so that
 * some players are team mates.
 * 
 * A player can only belong to a single team.
 * 
 * @author Arni Arent
 *
 */
public class TeamManager extends Manager {
	private Map<String, Bag<String>> playersByTeam;
	private Map<String, String> teamByPlayer;

	public TeamManager() {
		playersByTeam = new HashMap<>();
		teamByPlayer = new HashMap<>();
	}
	
	public String getTeam(String player) {
		return teamByPlayer.get(player);
	}
	
	public void setTeam(String player, String team) {
		removeFromTeam(player);
		
		teamByPlayer.put(player, team);
		
		Bag<String> players = playersByTeam.get(team);
		if(players == null) {
			players = new Bag<>();
			playersByTeam.put(team, players);
		}
		players.add(player);
	}
	
	public ImmutableBag<String> getPlayers(String team) {
		return playersByTeam.get(team);
	}
	
	public void removeFromTeam(String player) {
		String team = teamByPlayer.remove(player);
		if(team != null) {
			Bag<String> players = playersByTeam.get(team);
			if(players != null) {
				players.remove(player);
			}
		}
	}

	
	@Override
	protected void changed(Entity e) {
	}

	@Override
	protected void added(Entity e) {
	}

	@Override
	protected void deleted(Entity e) {
	}

	@Override
	protected void initialize() {
	}

}
