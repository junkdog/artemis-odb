package com.artemis;

/**
 * @author Daan van Yperen
 */
public class EntityWorld extends CosplayWorld<Entity> {
    public EntityWorld() {
        super(new WorldConfiguration(), Entity.class);
    }

    public EntityWorld(WorldConfiguration configuration) {
        super(configuration, Entity.class);
    }
}
