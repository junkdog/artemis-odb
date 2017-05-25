package com.artemis.link;

import com.artemis.Component;
import com.artemis.CosplayWorld;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

public class EntityFieldMutator<T extends Entity> implements UniFieldMutator {
    private CosplayWorld<T> world;

    @Override
    public int read(Component c, Field f) {
        try {
            Entity e = (Entity) f.get(c);
            return (e != null) ? e.getId() : -1;
        } catch (ReflectionException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void write(int value, Component c, Field f) {
        try {
            Entity e = (value != -1) ? world.getEntity(value) : null;
            f.set(c, e);
        } catch (ReflectionException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setWorld(World world) {
        this.world = (CosplayWorld<T>) world;
    }
}
