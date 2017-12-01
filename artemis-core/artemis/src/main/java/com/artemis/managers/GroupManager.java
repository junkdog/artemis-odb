package com.artemis.managers;

import static com.artemis.Aspect.all;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.borrowed.IntMap;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;

/**
 * If you need to group your entities together, e.g tanks going into "units"
 * group or explosions into "effects", then use this manager.
 * <p>
 * You must retrieve it using world instance.
 * </p>
 * <p>
 * A entity can be assigned to more than one group.
 * </p>
 *
 * @author Arni Arent
 */
public class GroupManager<T extends Entity> extends BaseSystem {
    private static final ImmutableBag<String> EMPTY_BAG = new Bag<>();

    /**
     * All entities and groups mapped with group names as key.
     */
    private final Map<String, IntBag> entitiesByGroup = new HashMap<>();
    /**
     * All entities and groups mapped with entities as key.
     */
    private final IntMap<Bag<String>> groupsByEntity = new IntMap<>();

    @Override
    protected void processSystem() {
    }

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(all())
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

    /**
     * Set the group of the entity.
     *
     * @param group group to add the entity into
     * @param e     entity to add into the group
     */
    public void add(T e, String group) {
        add(e.getId(), group);
    }

    public void add(final int entityId, String group) {
        IntBag entities = entitiesByGroup.get(group);
        if (entities == null) {
            entities = new IntBag();
            entitiesByGroup.put(group, entities);
        }
        if (!entities.contains(entityId))
            entities.add(entityId);

        Bag<String> groups = groupsByEntity.get(entityId);
        if (groups == null) {
            groups = new Bag<>();
            groupsByEntity.put(entityId, groups);
        }
        if (!groups.contains(group))
            groups.add(group);
    }

    /**
     * Set the group of the entity.
     *
     * @param g1 group to add the entity into
     * @param g2 group to add the entity into
     * @param e  entity to add into the group
     */

    public void add(T e, String g1, String g2) {
        add(e.getId(), g1, g2);
    }

    public void add(final int entityId, String g1, String g2) {
        add(entityId, g1);
        add(entityId, g2);
    }

    /**
     * Set the group of the entity.
     *
     * @param g1 group to add the entity into
     * @param g2 group to add the entity into
     * @param g3 group to add the entity into
     * @param e  entity to add into the group
     */

    public void add(T e, String g1, String g2, String g3) {
        add(e.getId(), g1, g2, g3);
    }

    public void add(final int entityId, String g1, String g2, String g3) {
        add(entityId, g1);
        add(entityId, g2);
        add(entityId, g3);
    }

    /**
     * Set the group of the entity.
     *
     * @param groups groups to add the entity into
     * @param e      entity to add into the group
     */

    public void add(T e, String... groups) {
        add(e.getId(), groups);
    }

    public void add(final int entityId, String... groups) {
        for (String group : groups) {
            add(entityId, group);
        }
    }

    /**
     * Remove the entity from the specified group.
     *
     * @param e     entity to remove from group
     * @param group group to remove the entity from
     */
    public void remove(T e, String group) {
        remove(e.getId(), group);
    }

    public void remove(final int entityId, String group) {
        IntBag entities = entitiesByGroup.get(group);
        if (entities != null) {
            entities.remove(entityId);
        }

        Bag<String> groups = groupsByEntity.get(entityId);
        if (groups != null) {
            groups.remove(group);
            if (groups.size() == 0)
                groupsByEntity.remove(entityId);
        }
    }

    /**
     * Removes the entity from the specified groups.
     *
     * @param e  entity to remove from group
     * @param g1 group to remove the entity from
     * @param g2 group to remove the entity from
     */

    public void remove(T e, String g1, String g2) {
        remove(e.getId(), g1, g2);
    }

    public void remove(final int entityId, String g1, String g2) {
        remove(entityId, g1);
        remove(entityId, g2);
    }

    /**
     * Removes the entity from the specified groups.
     *
     * @param e  entity to remove from group
     * @param g1 group to remove the entity from
     * @param g2 group to remove the entity from
     * @param g3 group to remove the entity from
     */

    public void remove(T e, String g1, String g2, String g3) {
        remove(e.getId(), g1, g2, g3);
    }

    public void remove(final int entityId, String g1, String g2, String g3) {
        remove(entityId, g1);
        remove(entityId, g2);
        remove(entityId, g3);
    }

    /**
     * Removes the entity from the specified groups
     *
     * @param e      entity to remove from group
     * @param groups groups to remove the entity from
     */

    public void remove(T e, String... groups) {
        remove(e.getId(), groups);
    }

    public void remove(final int entityId, String... groups) {
        for (String group : groups) {
            remove(entityId, group);
        }
    }

    /**
     * Remove the entity from all groups.
     *
     * @param e the entity to remove
     */
    public void removeFromAllGroups(T e) {
        removeFromAllGroups(e.getId());
    }

    public void removeFromAllGroups(final int entityId) {
        Bag<String> groups = groupsByEntity.get(entityId);
        if (groups == null)
            return;
        for (int i = 0, s = groups.size(); s > i; i++) {
            IntBag entities = entitiesByGroup.get(groups.get(i));
            if (entities != null) {
                entities.remove(entityId);
            }
        }
        groupsByEntity.remove(entityId);

    }

    public IntBag getEntityIds(final String group) {
        return entitiesByGroup.get(group);
    }

    /**
     * Get all groups the entity belongs to. An empty Bag is returned if the
     * entity doesn't belong to any groups.
     *
     * @param e the entity
     * @return the groups the entity belongs to.
     */
    public ImmutableBag<String> getGroups(T e) {
        return getGroups(e.getId());
    }

    public ImmutableBag<String> getGroups(final int entityId) {
        final Bag<String> groups = groupsByEntity.get(entityId);
        return groups != null ? groups : EMPTY_BAG;
    }

    /**
     * Checks if the entity belongs to any group.
     *
     * @param e the entity to check
     * @return true. if it is in any group, false if none
     */
    public boolean isInAnyGroup(T e) {
        return isInAnyGroup(e.getId());
    }

    public boolean isInAnyGroup(final int entityId) {
        return getGroups(entityId).size() > 0;
    }

    /**
     * Check if the entity is in the supplied group.
     *
     * @param group the group to check in
     * @param e     the entity to check for
     * @return true if the entity is in the supplied group, false if not
     */
    public boolean isInGroup(T e, String group) {
        return isInGroup(e.getId(),group);
    }

    public boolean isInGroup(final int entityId, String group) {
        if (group != null) {
            Bag<String> bag = groupsByEntity.get(entityId);
            if (bag != null) {
                Object[] groups = bag.getData();
                for (int i = 0, s = bag.size(); s > i; i++) {
                    String g = (String) groups[i];
                    if (group.equals(g)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void deleted(IntBag entities) {
        int[] ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            removeFromAllGroups(ids[i]);
        }
    }
}
