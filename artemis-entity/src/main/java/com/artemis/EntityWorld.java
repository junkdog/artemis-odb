package com.artemis;

/**
 * EntityWorld is a {@link World} with {@link Entity} support.
 *
 * Use this if you want to reference entities by {@link Entity} (and/or {@code int}).
 *
 * If you want to reference entities by {@code int} only use {@link World},
 * or if you want to subclass {@link Entity} extend {@link AbstractEntityWorld}.
 *
 * @see World for more information on usage.
 * @author Daan van Yperen
 */
public class EntityWorld extends AbstractEntityWorld<Entity> {

    /**
     * Creates a world with {@link Entity} support and without custom systems.
     * <p>
     * {@link EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
     * available by default.
     * </p>
     * @Deprecated Use {@link #EntityWorld(WorldConfiguration)} to create a world with your own systems.
     */
    @Deprecated
    public EntityWorld() {
        this(new WorldConfiguration());
    }

    /**
     * Creates a new world with {@link Entity} support.
     * <p>
     * {@link EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
     * available by default, on top of your own systems.
     * </p>
     *
     * @see WorldConfigurationBuilder
     * @see WorldConfiguration
     */
    public EntityWorld(WorldConfiguration configuration) {
        super(configuration,
                createEntityFactory(),
                Entity.class);
    }

    private static EntityFactory<EntityWorld, Entity> createEntityFactory() {
        return new EntityFactory<EntityWorld, Entity>() {
            @Override
            public Entity instance(EntityWorld world, int entityId) {
                return new Entity(world, entityId);
            }
        };
    };
}
