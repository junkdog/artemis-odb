package com.artemis;

import com.artemis.link.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

/**
 * @author Daan van Yperen
 */
public class CosplayWorld<T extends Entity> extends World {

    private final Class<T> entityClass;

    public CosplayWorld(WorldConfiguration configuration, Class<T> entityClass ) {
        super(configuration);
        this.entityClass = entityClass;
    }

    /**
     * Delete the entity from the world.
     *
     * @param e the entity to delete
     * @see #delete(int) recommended alternative.
     */
    public void deleteEntity(T e) {
        delete(e.id);
    }

    /**
     * Create and return a new or reused entity instance. Entity is
     * automatically added to the world.
     *
     * @return entity
     * @see #create() recommended alternative.
     */
    public T createEntity() {
        T e = getEntityManagerTyped().createEntityInstance();
        batchProcessor.changed.unsafeSet(e.getId());
        return e;
    }

    /**
     * Create and return an {@link T} wrapping a new or reused entity instance.
     * T is automatically added to the world.
     * <p>
     * Use {@link T#edit()} to set up your newly created entity.
     * <p>
     * You can also create entities using:
     * <ul>
     * <li>{@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.</li>
     * <li>{@link Archetype} Fastest, low level, no parameterized components.</li>
     * <li><a href="https://github.com/junkdog/artemis-odb/wiki/Serialization">Serialization</a>,
     * with a simple prefab-like class to parameterize the entities.</li>
     * </ul>
     *
     * @return entity
     * @see #create() recommended alternative.
     */
    public T createEntity(Archetype archetype) {
        T e = getEntityManagerTyped().createEntityInstance();

        int id = e.getId();
        archetype.transmuter.perform(id);
        cm.setIdentity(e.id, archetype.compositionId);

        batchProcessor.changed.unsafeSet(id);

        return e;
    }

    @SuppressWarnings("unchecked")
    private CosplayEntityManager<T> getEntityManagerTyped() {
        return (CosplayEntityManager<T>) em;
    }

    @Override
    public Class getEntityClass() {
        return entityClass;
    }

    @Override
    public LinkFactory.ReflexiveMutators getReflextiveMutators() {

        class CosplayWorldReflexiveMutators implements LinkFactory.ReflexiveMutators {
            private final EntityFieldMutator entityField;
            private final IntFieldMutator intField;
            private final IntBagFieldMutator intBagField;
            private final EntityBagFieldMutator entityBagField;
            private final World world;

            private CosplayWorldReflexiveMutators(World world) {
                this.world = world;

                entityField = new EntityFieldMutator<T>();
                entityField.setWorld(world);

                intField = new IntFieldMutator();
                intField.setWorld(world);

                intBagField = new IntBagFieldMutator();
                intBagField.setWorld(world);

                entityBagField = new EntityBagFieldMutator<T>();
                entityBagField.setWorld(world);
            }

            public UniLinkSite withMutator(UniLinkSite linkSite) {
                if (linkSite.fieldMutator != null)
                    return linkSite;

                Class type = linkSite.field.getType();
                if (world.getEntityClass() == type) {
                    linkSite.fieldMutator = entityField;
                } else if (int.class == type) {
                    linkSite.fieldMutator = intField;
                } else {
                    throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
                }

                return linkSite;
            }

            public MultiLinkSite withMutator(MultiLinkSite linkSite) {
                if (linkSite.fieldMutator != null)
                    return linkSite;

                Class type = linkSite.field.getType();
                if (IntBag.class == type) {
                    linkSite.fieldMutator = intBagField;
                } else if (Bag.class == type) {
                    linkSite.fieldMutator = entityBagField;
                } else {
                    throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
                }

                return linkSite;
            }
        }

        return new CosplayWorldReflexiveMutators(this);
    }

    /**
     * Get entity with the specified id.
     * <p>
     * Resolves entity id to the unique entity instance. <em>This method may
     * return an entity even if it isn't active in the world.</em> Make sure to
     * not retain id's of deleted entities.
     *
     * @param entityId the entities id
     * @return the specific entity
     */
    public T getEntity(int entityId) {
        return getEntityManagerTyped().getEntity(entityId);
    }
}
