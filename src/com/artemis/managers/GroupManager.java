package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

/**
 * If you need to group your entities together, e.g. tanks going into "units" group or explosions into "effects",
 * then use this manager. You must retrieve it using world instance.
 * 
 * A entity can be assigned to more than one group.
 * 
 * @author Arni Arent
 *
 */
public class GroupManager extends Manager {
	private Map<String, Bag<Entity>> entitiesByGroup;
	private Map<Entity, Bag<String>> groupsByEntity;

	public GroupManager() {
		entitiesByGroup = new HashMap<String, Bag<Entity>>();
		groupsByEntity = new HashMap<Entity, Bag<String>>();
	}
	

	@Override
	protected void initialize() {
	}
	
	
	/**
	 * Set the group of the entity.
	 * 
	 * @param group group to add the entity into.
	 * @param e entity to add into the group.
	 */
	public void add(Entity e, String group) {
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities == null) {
			entities = new Bag<Entity>();
			entitiesByGroup.put(group, entities);
		}
		entities.add(e);
		
		Bag<String> groups = groupsByEntity.get(e);
		if(groups == null) {
			groups = new Bag<String>();
			groupsByEntity.put(e, groups);
		}
		groups.add(group);
	}
	
	/**
	 * Remove the entity from the specified group.
	 * @param e
	 * @param group
	 */
	public void remove(Entity e, String group) {
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities != null) {
			entities.remove(e);
		}
		
		Bag<String> groups = groupsByEntity.get(e);
		if(groups != null) {
			groups.remove(group);
		}
	}
	
	public void removeFromAllGroups(Entity e) {
		Bag<String> groups = groupsByEntity.get(e);
		if(groups != null) {
			for(int i = 0; groups.size() > i; i++) {
				Bag<Entity> entities = entitiesByGroup.get(groups.get(i));
				if(entities != null) {
					entities.remove(e);
				}
			}
			groups.clear();
		}
	}
	
	/**
	 * Get all entities that belong to the provided group.
	 * @param group name of the group.
	 * @return read-only bag of entities belonging to the group.
	 */
	public ImmutableBag<Entity> getEntities(String group) {
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities == null) {
			entities = new Bag<Entity>();
			entitiesByGroup.put(group, entities);
		}
		return entities;
	}
	
	/**
	 * @param e entity
	 * @return the groups the entity belongs to, null if none.
	 */
	public ImmutableBag<String> getGroups(Entity e) {
		return groupsByEntity.get(e);
	}
	
	/**
	 * Checks if the entity belongs to any group.
	 * @param e the entity to check.
	 * @return true if it is in any group, false if none.
	 */
	public boolean isInAnyGroup(Entity e) {
		return getGroups(e) != null;
	}
	
	/**
	 * Check if the entity is in the supplied group.
	 * @param group the group to check in.
	 * @param e the entity to check for.
	 * @return true if the entity is in the supplied group, false if not.
	 */
	public boolean inInGroup(Entity e, String group) {
		Bag<String> groups = groupsByEntity.get(e);
		return groups.contains(group);
	}

	@Override
	public void deleted(Entity e) {
		removeFromAllGroups(e);
	}
	
}
