package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;


/**
 * If you need to group your entities together, e.g tanks going into "units"
 * group or explosions into "effects", then use this manager.
 * <p>
 * You must retrieve it using world instance.
 * </p><p>
 * A entity can be assigned to more than one group.
 * </p>
 * 
 * @author Arni Arent
 */
public class GroupManager extends Manager {
	private static final ImmutableBag<String> EMPTY_BAG = new Bag<String>();
	
	/** All entities and groups mapped with group names as key. */
	private final Map<String, Bag<Entity>> entitiesByGroup;
	/** All entities and groups mapped with entities as key. */
	private final Map<Entity, Bag<String>> groupsByEntity;


	/**
	 * Creates a new GroupManager instance.
	 */
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
	 * @param group
	 *			group to add the entity into
	 * @param e
	 *			entity to add into the group
	 */
	public void add(Entity e, String group) {
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities == null) {
			entities = new Bag<Entity>();
			entitiesByGroup.put(group, entities);
		}
		if (!entities.contains(e)) entities.add(e);
		
		Bag<String> groups = groupsByEntity.get(e);
		if(groups == null) {
			groups = new Bag<String>();
			groupsByEntity.put(e, groups);
		}
		if (!groups.contains(group)) groups.add(group);
	}
	
	/**
	 * Remove the entity from the specified group.
	 *
	 * @param e
	 *			entity to remove from group
	 * @param group
	 *			group to remove the entity from
	 */
	public void remove(Entity e, String group) {
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities != null) {
			entities.remove(e);
		}
		
		Bag<String> groups = groupsByEntity.get(e);
		if(groups != null) {
			groups.remove(group);
			if (groups.size() == 0) groupsByEntity.remove(e);
		}
	}

	/**
	 * Remove the entity from all groups.
	 *
	 * @param e
	 *			the entity to remove
	 */
	public void removeFromAllGroups(Entity e) {
		Bag<String> groups = groupsByEntity.get(e);
		if(groups == null) return;
		for(int i = 0, s = groups.size(); s > i; i++) {
			Bag<Entity> entities = entitiesByGroup.get(groups.get(i));
			if(entities != null) {
				entities.remove(e);
			}
		}
		groupsByEntity.remove(e);
	}
	
	/**
	 * Get all entities that belong to the provided group.
	 *
	 * @param group
	 *			name of the group
	 *
	 * @return read-only bag of entities belonging to the group
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
	 * Get all groups the entity belongs to. An empty Bag is returned
	 * if the entity doesn't belong to any groups.
	 *
	 * @param e
	 *			the entity
	 *
	 * @return the groups the entity belongs to.
	 */
	public ImmutableBag<String> getGroups(Entity e) {
		Bag<String> groups = groupsByEntity.get(e);
		return groups != null ? groups : EMPTY_BAG;
	}
	
	/**
	 * Checks if the entity belongs to any group.
	 *
	 * @param e
	 *			the entity to check
	 *
	 * @return true. if it is in any group, false if none
	 */
	public boolean isInAnyGroup(Entity e) {
		return getGroups(e).size() > 0;
	}
	
	/**
	 * Check if the entity is in the supplied group.
	 *
	 * @param group
	 *			the group to check in
	 * @param e
	 *			the entity to check for
	 *
	 * @return true if the entity is in the supplied group, false if not
	 */
	public boolean isInGroup(Entity e, String group) {
		if(group != null) {
			Bag<String> bag = groupsByEntity.get(e);
			if (bag != null) {
				Object[] groups = bag.getData();
				for(int i = 0, s = bag.size(); s > i; i++) {
					String g = (String)groups[i];
					if(group.equals(g)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Removes the entity from all groups.
	 *
	 * @param e
	 *			the deleted entity
	 */
	@Override
	public void deleted(Entity e) {
		removeFromAllGroups(e);
	}
	
}
