package com.artemis.managers;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.*;
import com.artemis.utils.IntBag;

import static com.artemis.Aspect.all;


/**
 * If you need to tag any entity, use this.
 * <p>
 * A typical usage would be to tag entities such as "PLAYER", "BOSS" or
 * something that is very unique.
 * </p>
 * 
 * @author Arni Arent
 */
public class TagManager extends BaseSystem {

	/** Tags mapped to entities. */
	private final Map<String, Entity> entitiesByTag;
	/** Tagged entities mapped to tags. */
	private final Map<Entity, String> tagsByEntity;

	private final BitSet registered;

	/**
	 * Creates a new TagManager.
	 */
	public TagManager() {
		entitiesByTag = new HashMap<String, Entity>();
		tagsByEntity = new HashMap<Entity, String>();
		registered = new BitSet();
	}

	@Override
	protected void processSystem() {}

	@Override
	protected void initialize() {
		world.getAspectSubscriptionManager()
				.get(all())
				.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
					@Override
					public void inserted(IntBag entities) {}

					@Override
					public void removed(IntBag entities) {
						deleted(entities);
					}
				});
	}

	void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			int id = ids[i];
			if (registered.get(id)) {
				String removedTag = tagsByEntity.remove(world.getEntity(id));
				entitiesByTag.remove(removedTag);
				registered.clear(id);
			}
		}
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
		unregister(tag);
		if (getTag(e) != null) {
			unregister(getTag(e));
		}

		entitiesByTag.put(tag, e);
		tagsByEntity.put(e, tag);
		registered.set(e.getId());
	}

	public void register(String tag, int entityId) {
		register(tag, world.getEntity(entityId));
	}

	/**
	 * Remove a tag from the entity being tagged with it.
	 *
	 * @param tag
	 *			the tag to remove
	 */
	public void unregister(String tag) {
		Entity removed = entitiesByTag.remove(tag);
		if (removed != null) {
			tagsByEntity.remove(removed);
			registered.clear(removed.getId());
		}
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
}
