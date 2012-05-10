package com.artemis;

import java.util.HashMap;
import java.util.Map;

/**
 * If you need to tag any entity, use this. A typical usage would be to tag
 * entities such as "PLAYER". After creating an entity call register().
 * 
 * @author Arni Arent
 *
 */
public class TagManager {
	private World world;
	private Map<String, Entity> entityByTag;

	public TagManager(World world) {
		this.world = world;
		entityByTag = new HashMap<String, Entity>();
	}

	public void register(String tag, Entity e) {
		entityByTag.put(tag, e);
	}

	public void unregister(String tag) {
		entityByTag.remove(tag);
	}

	public boolean isRegistered(String tag) {
		return entityByTag.containsKey(tag);
	}

	public Entity getEntity(String tag) {
		return entityByTag.get(tag);
	}
	
	protected void remove(Entity e) {
		entityByTag.values().remove(e);
	}

}
