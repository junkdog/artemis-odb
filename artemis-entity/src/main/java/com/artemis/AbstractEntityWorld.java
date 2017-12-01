package com.artemis;

import com.artemis.link.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import static com.artemis.WorldConfiguration.ENTITY_MANAGER_IDX;

/**
 * Abstract class for World implementations that supports object references.
 *
 * Allows you to build a world with a subclass of {@link Entity}.
 * See {@link EntityWorld} for example implementation. Fluid API also
 * extends this world.
 *
 * Supports referencing to entities by {@code int} and T.
 *
 * If you want to reference entities by {@code int} only use {@link World},
 * or if you want to subclass {@link Entity} extend {@link AbstractEntityWorld}.
 *
 * @param <T> Entity class to be used in systems.
 * @see World for more information on usage.
 * @author Daan van Yperen
 */
public abstract class AbstractEntityWorld<T extends Entity> extends World implements SerializationEntityProvider<T> {

    /**
     * Instance world with both {@code int} and object reference support.
     *
     * @param configuration Configuration to base world upon.
     * @param entityFactory Factory to create instances of T.
     * @param entityType T.class (for example, Entity.class)
     */
    protected AbstractEntityWorld(WorldConfiguration configuration, EntityFactory<? extends World, T> entityFactory, Class<? extends T> entityType) {
        super(appendConfiguration(configuration, entityFactory, entityType));
    }

    /**
     * Enrich configuration with custom entity manager that will instance the entity correctly.
     */
    protected static <W extends World, T extends Entity> WorldConfiguration appendConfiguration(WorldConfiguration configuration, EntityFactory<W, T> entityFactory, Class<? extends Entity> entityType) {
        if ( entityType == null ) {
            throw new NullPointerException("Please supply class of Entity.");
        }
        if ( entityFactory == null ) {
            throw new NullPointerException("EntityFactory is required to create instances of " + entityType);
        }
        WorldConfiguration config = configuration.setEntityType(entityType);

        // provide component mapper factory if none specified by the user.
        if (config.getComponentMapperFactory() == null) {
            config.setComponentMapperFactory(createAbstractEntityComponentMapperFactory());
        }

        // TODO: move to configuration.
        config.systems.set(ENTITY_MANAGER_IDX, new CosplayEntityManager<>(config.expectedEntityCount, entityFactory));
        return config;
    }

    private static ComponentMapperFactory createAbstractEntityComponentMapperFactory() {
        return new ComponentMapperFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public ComponentMapper instance(Class<? extends Component> type, World world) {
                return new CosplayComponentMapper(type, world);
            }
        };
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
