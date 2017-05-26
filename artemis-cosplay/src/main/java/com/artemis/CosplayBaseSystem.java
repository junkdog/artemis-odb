package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.reflect.Method;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.utils.reflect.ClassReflection.getMethod;

/**
 * @author Daan van Yperen
 */
public abstract class CosplayBaseSystem<T extends Entity> extends BaseSystem {

    @SkipWire
    protected CosplayWorld<T> worldTyped;

    /**
     * Set the world this system works on.
     *
     * @param world the world to set
     */
    @SuppressWarnings("unchecked")
    protected void setWorld(World world) {
        this.worldTyped = (CosplayWorld<T>) world;
        this.world = world;
    }

    @SuppressWarnings("unchecked")
    public CosplayWorld<T> getWorldTyped() {
        return worldTyped;
    }

    protected boolean implementsObserver(String methodName) {

        try {
            Method method = getMethod(getClass(), methodName, world.getEntityClass());
            Class declarer = method.getDeclaringClass();
            return !(Manager.class.equals(declarer) || EntitySystem.class.equals(declarer));
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
}
