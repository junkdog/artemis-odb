package com.artemis;

/**
 * @author Daan van Yperen
 */
public class EntityWorld extends CosplayWorld<Entity> {

    public EntityWorld() {
        this(new WorldConfiguration());
    }

    public EntityWorld(WorldConfiguration configuration) {
        super(configuration,
                new EntityFactory<EntityWorld, Entity>() {
                    @Override
                    public Entity instance(EntityWorld world, int entityId) {
                        return new Entity(world, entityId);
                    }
                },
                Entity.class);
    };
}
