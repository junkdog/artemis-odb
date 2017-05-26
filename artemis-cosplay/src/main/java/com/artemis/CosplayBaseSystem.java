package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.reflect.Method;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.utils.reflect.ClassReflection.getMethod;

/**
 * @author Daan van Yperen
 */
public abstract class CosplayBaseSystem<T extends Entity> extends BaseSystem<CosplayWorld<T>> {

    protected boolean implementsObserver(String methodName) {
        try {
            Method method = getMethod(getClass(), methodName, world.getEntityClass());
            Class declarer = method.getDeclaringClass();
            return !(Manager.class.equals(declarer) || EntitySystem.class.equals(declarer));
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fast but unsafe retrieval of a reference object for this entity id.
     *
     * Convenience wrapper for world.getEntity();
     *
     * This method trades performance for safety.
     *
     * User is expected to avoid calling this method on recently (in same system) removed or
     * retired entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
     * or a partially recycled entity if called on removed or non-existent ids.
     *
     * @param entityId the entity id to fetch
     * @return pre-existing entity of type T
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */
    public T E(int entityId) {
        return world.getEntity(entityId);
    }

    /**
     * Method to create and return a new or reused entity instance.
     *
     * Convenience wrapper for world.createentity();
     *
     * Entity is automatically added to the world.
     * <p>
     * @return new entity of type T.
     */
    public T E() {
        return world.createEntity();
    }
}
