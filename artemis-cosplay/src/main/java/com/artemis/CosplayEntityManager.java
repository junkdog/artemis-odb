package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;

/**
 * @author Daan van Yperen
 */
@SkipWire
public abstract class CosplayEntityManager<T extends Entity> extends EntityManager {
    /**
     * Contains all entities in the manager.
     */
    final Bag<T> entities;

    public CosplayEntityManager(int initialContainerSize) {
        super(initialContainerSize);
        entities = new Bag<>(initialContainerSize);
    }

    /**
     * Create a new entity.
     *
     * @return a new entity
     */
    protected T createEntityInstance() {
        return obtain(super.create());
    }

    /**
     * Resolves entity id to the unique entity instance. <em>This method may
     * return an entity even if it isn't active in the world, </em> use
     * {@link #isActive(int)} if you need to check whether the entity is active or not.
     *
     * @param entityId the entities id
     * @return the entity
     */
    protected T getEntity(int entityId) {
        return entities.get(entityId);
    }

    @Override
    protected void actuallyReset() {
        super.actuallyReset();
        entities.clear();
    }

    @Override
    protected int create() {
        return obtain(super.create()).id;
    }

    /**
     * Instantiates an Entity without registering it into the world.
     *
     * @param id The ID to be set on the Entity
     */
    private T createEntity(int id) {
        T e = createInstance(world, id);

        // can't use unsafe set, as we need to track highest id
        // for faster iteration when syncing up new subscriptions
        // in ComponentManager#synchronize
        entities.set(e.id, e);

        return e;
    }

    /**
     * @return new Entity(world, id);
     */
    protected abstract T createInstance(World world, int id);

    @Override
    protected void growEntityStores() {
        super.growEntityStores();
        entities.ensureCapacity(desiredCapacity);
    }

    private T obtain(int i) {
        if (i >= entities.getCapacity()) {
            growEntityStores();
        }
        final T result = entities.get(i);
        if (result == null) {
            return createEntity(i);
        }
        return result;
    }
}
