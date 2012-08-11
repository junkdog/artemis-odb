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
 * A entity can only belong to one group at a time.
 * 
 * @author Arni Arent
 *
 */
public class GroupManager extends Manager {
	private Bag<Entity> EMPTY_BAG;
	private Map<String, Bag<Entity>> entitiesByGroup;
	private Bag<String> groupByEntity;

	public GroupManager() {
		entitiesByGroup = new HashMap<String, Bag<Entity>>();
		groupByEntity = new Bag<String>();
		EMPTY_BAG = new Bag<Entity>();
	}
	

	@Override
	protected void initialize() {
	}
	
	
	/**
	 * Set the group of the entity.
	 * 
	 * @param group group to set the entity into.
	 * @param e entity to set into the group.
	 */
	public void set(String group, Entity e) {
		deleted(e); // Entity can only belong to one group.
		
		Bag<Entity> entities = entitiesByGroup.get(group);
		if(entities == null) {
			entities = new Bag<Entity>();
			entitiesByGroup.put(group, entities);
		}
		entities.add(e);
		
		groupByEntity.set(e.getId(), group);
	}
	
	/**
	 * Get all entities that belong to the provided group.
	 * @param group name of the group.
	 * @return read-only bag of entities belonging to the group.
	 */
	public ImmutableBag<Entity> getEntities(String group) {
		Bag<Entity> bag = entitiesByGroup.get(group);
		if(bag == null)
			return EMPTY_BAG;
		return bag;
	}
	
	/**
	 * @param e entity
	 * @return the name of the group that this entity belongs to, null if none.
	 */
	public String getGroupOf(Entity e) {
		if(e.getId() < groupByEntity.getCapacity()) {
			return groupByEntity.get(e.getId());
		}
		return null;
	}
	
	/**
	 * Checks if the entity belongs to any group.
	 * @param e the entity to check.
	 * @return true if it is in any group, false if none.
	 */
	public boolean isGrouped(Entity e) {
		return getGroupOf(e) != null;
	}
	
	/**
	 * Check if the entity is in the supplied group.
	 * @param group the group to check in.
	 * @param e the entity to check for.
	 * @return true if the entity is in the supplied group, false if not.
	 */
	public boolean isInGroup(String group, Entity e) {
		return group != null && group.equalsIgnoreCase(getGroupOf(e));
	}

	@Override
	protected void added(Entity e) {
	}

	@Override
	protected void deleted(Entity e) {
		if(e.getId() < groupByEntity.getCapacity()) {
			String group = groupByEntity.get(e.getId());
			if(group != null) {
				groupByEntity.set(e.getId(), null);
				
				Bag<Entity> entities = entitiesByGroup.get(group);
				if(entities != null) {
					entities.remove(e);
				}
			}
		}
	}
	
	@Override
	protected void changed(Entity e) {
	}

}
