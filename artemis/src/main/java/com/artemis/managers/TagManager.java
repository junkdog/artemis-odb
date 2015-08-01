package com.artemis.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;


/**
 * If you need to tag any entity, use this.
 * <p>
 * A typical usage would be to tag entities such as "PLAYER", "BOSS" or
 * something that is very unique.
 * </p>
 * 
 * @author Arni Arent
 */
public class TagManager extends Manager {

	/** Tags mapped to entities. */
	private final Map<String, Integer> entitiesByTag;
	/** Tagged entities mapped to tags. */
	private final Map<Integer, String> tagsByEntity;
	
	/** Flyweight helper for entities. */
	private Entity flyweight;


	/**
	 * Creates a new TagManager.
	 */
	public TagManager() {
		entitiesByTag = new HashMap<String, Integer>();
		tagsByEntity = new HashMap<Integer, String>();
	}


	/**
	 * Tag an entity.
	 * <p>
	 * Each tag can only be given to one entity at a time.
	 * </p>
	 *
	 * @param tag
	 *			the tag
	 * @param entityId
	 *			the entity id to get tagged
	 */
	public void register(String tag, int entityId) {
		entitiesByTag.put(tag, entityId);
		tagsByEntity.put(entityId, tag);
	}
	
	/**
	 * Tag an entity.
	 * <p>
	 * Each tag can only be given to one entity at a time.
	 * </p>
	 *
	 * @param tag
	 *			the tag
	 * @param entityId
	 *			the entity id to get tagged
	 */
	public void register(String tag, Entity entity) {
		entitiesByTag.put(tag, entity.id);
		tagsByEntity.put(entity.id, tag);
	}

	/**
	 * Remove a tag from the entity being tagged with it.
	 *
	 * @param tag
	 *			the tag to remove
	 */
	public void unregister(String tag) {
		tagsByEntity.remove(entitiesByTag.remove(tag));
	}

	/**
	 * Check if a tag is in use.
	 *
	 * @param tag
	 *			the tag to check
	 *
	 * @return {@code true} if the tag is in use
	 */
	public boolean isRegistered(String tag) {
		return entitiesByTag.containsKey(tag);
	}
	
	/**
	 * Get the entity <b>flyweight</b> tagged with the given tag.
	 *
	 * @param tag
	 *			the tag the entity is tagged with
	 *
	 * @return the tagged entity flyweight
	 */
	public Entity getEntity(String tag) {
		Integer id = entitiesByTag.get(tag);

		if (id == null) {
			return null;
		}
		else {
			flyweight.id = id.intValue();
		}
		return flyweight;
	}

	/**
	 * Get the entity id tagged with the given tag.
	 *
	 * @param tag
	 *			the tag the entity is tagged with
	 *
	 * @return the tagged entity id
	 */
	public int getEntityId(String tag) {
		return entitiesByTag.get(tag);
	}

	/**
	 * Get the tag the given entity is tagged with.
	 *
	 * @param entity
	 *			the entity
	 *
	 * @return the tag
	 */
	public String getTag(Entity entity) {
		return tagsByEntity.get(entity.id);
	}
	
	/**
	 * Get the tag the given entity is tagged with.
	 *
	 * @param entity
	 *			the entity ic
	 *
	 * @return the tag
	 */
	public String getTag(int entityId) {
		return tagsByEntity.get(entityId);
	}

	/**
	 * Get all used tags.
	 *
	 * @return all used tags as collection
	 */
	public Collection<String> getRegisteredTags() {
		return tagsByEntity.values();
	}

	/**
	 * If the entity gets deleted, remove the tag used by it.
	 *
	 * @param e
	 *			the deleted entity
	 */
	@Override
	public void deleted(Entity e) {
		String removedTag = tagsByEntity.remove(e.id);
		if(removedTag != null) {
			entitiesByTag.remove(removedTag);
		}
	}


	@Override
	protected void initialize() {
		flyweight = Entity.createFlyweight(world);
	}

}
