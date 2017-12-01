package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;

/**
 * @author Daan van Yperen
 */
@SkipWire
public class CosplayEntityManager<T extends Entity> extends EntityManager {
    /**
     * Contains all entities in the manager.
     */
    final Bag<T> entities;
    private final EntityFactory entityFactory;

    public CosplayEntityManager(int initialContainerSize, EntityFactory<? extends World, T> entityFactory) {
        super(initialContainerSize);
        entities = new Bag<>(initialContainerSize);
        this.entityFactory = entityFactory;
    }

    /**
     * Method to create and return a new or reused entity instance.
     *
     * Entity is automatically added to the world.
     * <p>
     * @return new entity of type T.
     */
    protected T createEntityInstance() {
        return obtain(super.create());
    }

    /**
     * Fast but unsafe retrieval of a reference object for this entity id.
     *
     * This method trades performance for safety.
     *
     * User is expected to avoid calling this method on recently (in same system) removed or
     * retired entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
     * or a partially recycled entity if called on removed or non-existent ids.
     *
     * use {@link #isActive(int)} if you need to check whether the entity is active or not.
     *
     * @param entityId the entity id to fetch
     * @return the entity of type T
     * @throws java.lang.ArrayIndexOutOfBoundsException
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
     * Method to create and return a new or reused entity id.
     *
     * Entity is automatically added to the world. Creates reference entity
     * as well so provides no performance benefit.
     *
     * @return new entity id.
     */
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
    @SuppressWarnings("unchecked")
    protected T createInstance(World world, int id) {
        return (T) entityFactory.instance(world, id);
    }

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
