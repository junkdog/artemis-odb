package com.artemis.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;


/**
 * If you need to tag any entity, use this. A typical usage would be to tag
 * entities such as "PLAYER", "BOSS" or something that is very unique.
 * 
 * @author Arni Arent
 *
 */
public class TagManager extends Manager {
	private Map<String, Entity> entitiesByTag;
	private Map<Entity, String> tagsByEntity;

	public TagManager() {
		entitiesByTag = new HashMap<String, Entity>();
		tagsByEntity = new HashMap<Entity, String>();
	}

	public void register(String tag, Entity e) {
		entitiesByTag.put(tag, e);
		tagsByEntity.put(e, tag);
	}

	public void unregister(String tag) {
		tagsByEntity.remove(entitiesByTag.remove(tag));
	}

	public boolean isRegistered(String tag) {
		return entitiesByTag.containsKey(tag);
	}

	public Entity getEntity(String tag) {
		return entitiesByTag.get(tag);
	}
	
	public Collection<String> getRegisteredTags() {
		return tagsByEntity.values();
	}
	
	@Override
	public void deleted(Entity e) {
		String removedTag = tagsByEntity.remove(e);
		if(removedTag != null) {
			entitiesByTag.remove(removedTag);
		}
	}

	@Override
	protected void initialize() {
	}

}
