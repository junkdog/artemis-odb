package com.artemis.managers;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.World;


/**
 * If you need to tag any entity, use this. A typical usage would be to tag
 * entities such as "PLAYER". After creating an entity call register().
 * 
 * @author Arni Arent
 *
 */
public class TagManager extends Manager {
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
