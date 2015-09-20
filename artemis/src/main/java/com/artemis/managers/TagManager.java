package com.artemis.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.artemis.EntityHelper;
import com.artemis.Manager;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


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
	private final ObjectIntMap<String> entitiesByTag;
	/** Tagged entities mapped to tags. */
	private final IntMap< String> tagsByEntity;

	/**
	 * Creates a new TagManager.
	 */
	public TagManager() {
		entitiesByTag = new ObjectIntMap<String>();
		tagsByEntity = new IntMap< String>();
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
	public void register(String tag, int e) {
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
		tagsByEntity.remove(entitiesByTag.remove(tag, EntityHelper.NO_ENTITY));
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
	public int getEntity(String tag) {
		return entitiesByTag.get(tag, EntityHelper.NO_ENTITY);
	}

	/**
	 * Get the tag the given entity is tagged with.
	 *
	 * @param entity
	 *			the entity
	 *
	 * @return the tag
	 */
	public String getTag(int entity) {
		return tagsByEntity.get(entity);
	}

	/**
	 * Get all used tags.
	 *
	 * @return all used tags as collection
	 */
	@Deprecated
	public Collection<String> getRegisteredTags() {
		// @todo int fix performance!
		return Arrays.asList(entitiesByTag.keys().toArray().toArray());
	}

	/**
	 * If the entity gets deleted, remove the tag used by it.
	 *
	 * @param entityId
	 *			the deleted entity
	 */
	@Override
	public void deleted(int entityId) {
		String removedTag = tagsByEntity.remove(entityId);
		if(removedTag != null) {
			entitiesByTag.remove(removedTag, EntityHelper.NO_ENTITY);
		}
	}

}
