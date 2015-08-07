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
	private final Map<String, Entity> entitiesByTag;
	/** Tagged entities mapped to tags. */
	private final Map<Entity, String> tagsByEntity;


	private Entity flyweight;

	/**
	 * Creates a new TagManager.
	 */
	public TagManager() {
		entitiesByTag = new HashMap<String, Entity>();
		tagsByEntity = new HashMap<Entity, String>();
	}


	/**
	 * Tag an entity.
	 * <p>
	 * Each tag can only be given to one entity at a time.
	 * </p>
	 *
	 * @param tag
	 *			the tag
	 * @param e
	 *			the entity to get tagged
	 */
	public void register(String tag, Entity e) {
		entitiesByTag.put(tag, e);
		tagsByEntity.put(e, tag);
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
	 * Get the entity tagged with the given tag.
	 *
	 * @param tag
	 *			the tag the entity is tagged with
	 *
	 * @return the tagged entity
	 */
	public Entity getEntity(String tag) {
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
		return tagsByEntity.get(entity);
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
	 * @param entityId
	 *			the deleted entity
	 */
	@Override
	public void deleted(int entityId) {
		flyweight.id = entityId;
		String removedTag = tagsByEntity.remove(flyweight);
		if(removedTag != null) {
			entitiesByTag.remove(removedTag);
		}
	}


	@Override
	protected void initialize() {
		flyweight = Entity.createFlyweight(world);
	}

}
