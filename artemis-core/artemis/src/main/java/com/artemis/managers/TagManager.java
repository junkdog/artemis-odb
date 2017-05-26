package com.artemis.managers;

import com.artemis.borrowed.IntMap;
import com.artemis.borrowed.ObjectIntMap;
import com.artemis.utils.BitVector;

import java.util.*;

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
public class TagManager<T extends Entity> extends BaseSystem {

    private final ObjectIntMap<String> entitiesByTag = new ObjectIntMap<>();
    private final IntMap<String> tagsByEntity = new IntMap<>();

    private final BitVector registered = new BitVector();

    @Override
    protected void processSystem() {
    }

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager()
                .get(all())
                .addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                    @Override
                    public void inserted(IntBag entities) {
                    }

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
                String removedTag = tagsByEntity.remove(id);
                entitiesByTag.remove(removedTag, -1);
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
     * @param tag the tag
     * @param e   the entity to get tagged
     */
    public void register(String tag, T e) {
        register(tag, e.getId());
    }

    public void register(String tag, int entityId) {
        unregister(tag);
        if (getTag(entityId) != null) {
            unregister(getTag(entityId));
        }

        entitiesByTag.put(tag, entityId);
        tagsByEntity.put(entityId, tag);
        registered.set(entityId);
    }

    /**
     * Remove a tag from the entity being tagged with it.
     *
     * @param tag the tag to remove
     */
    public void unregister(String tag) {
        int removed = entitiesByTag.remove(tag, -1);
        if (removed != -1) {
            tagsByEntity.remove(removed);
            registered.clear(removed);
        }
    }

    /**
     * Check if a tag is in use.
     *
     * @param tag the tag to check
     * @return {@code true} if the tag is in use
     */
    public boolean isRegistered(String tag) {
        return entitiesByTag.containsKey(tag);
    }


    public int getEntityId(String tag) {
        return entitiesByTag.get(tag, -1);
    }

    /**
     * Get the tag the given entity is tagged with.
     *
     * @param entity the entity
     * @return the tag
     */
    public String getTag(T entity) {
        return getTag(entity.getId());
    }

    public String getTag(int entityId) {
        return  tagsByEntity.get(entityId);
    }

    /**
     * Get all used tags.
     *
     * @return all used tags as collection
     */
    public Collection<String> getRegisteredTags() {
        // @TODO Cleanup crew!
        Collection<String> result = new ArrayList<>();
        while (tagsByEntity.values().hasNext()) {
            result.add(tagsByEntity.values().next());
        }
        return result;
    }
}
