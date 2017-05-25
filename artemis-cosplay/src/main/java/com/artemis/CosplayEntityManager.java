package com.artemis;

import com.artemis.utils.Bag;

/**
 * @author Daan van Yperen
 */
public abstract class CosplayEntityManager<T extends Entity> extends EntityManager {
    /**
     * Contains all entities in the manager.
     */
    final Bag<T> entities;

    public CosplayEntityManager(int initialContainerSize, Bag<T> entities) {
        super(initialContainerSize);
        entities = new Bag<>(initialContainerSize);
        this.entities = entities;
    }

    /**
     * Create a new entity.
     *
     * @return a new entity
     */
    protected T createEntityInstance() {
        return obtain();
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

    /**
     * Instantiates an Entity without registering it into the world.
     *
     * @param id The ID to be set on the Entity
     */
    private T createEntity(int id) {
        T e = createInstance(world, id);
        if (e.id >= entities.getCapacity()) {
            growEntityStores();
        }

        // can't use unsafe set, as we need to track highest id
        // for faster iteration when syncing up new subscriptions
        // in ComponentManager#synchronize
        entities.set(e.id, e);

        return e;
    }

    /** @return new Entity(world, id); */
    protected abstract T createInstance(World world, int id);

    @Override
    protected void growEntityStores() {
        super.growEntityStores();
        int newSize = 2 * entities.getCapacity();
        entities.ensureCapacity(newSize);
    }

    private T obtain() {
        final int i = create();
        final T result = entities.get(i);
        if (result == null) {
            return createEntity(i);
        }
        return result;
    }
}
