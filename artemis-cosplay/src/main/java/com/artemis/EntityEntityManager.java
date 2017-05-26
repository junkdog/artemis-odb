package com.artemis;

import com.artemis.annotations.SkipWire;

/**
 * @author Daan van Yperen
 */
@SkipWire
public class EntityEntityManager extends CosplayEntityManager<Entity> {
    public EntityEntityManager(int initialContainerSize) {
        super(initialContainerSize);
    }

    @Override
    protected Entity createInstance(World world, int id) {
        return new Entity(world, id);
    }
}
