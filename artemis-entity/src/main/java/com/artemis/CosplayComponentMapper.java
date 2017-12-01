package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;

/**
 * @author Daan van Yperen
 */
public class CosplayComponentMapper<A extends Component, T extends Entity> extends ComponentMapper<A> {

    public CosplayComponentMapper(Class<A> type, World world) {
        super(type, world);
    }

    /**
     * Fast but unsafe retrieval of a component for this entity.
     * <p>
     * This method trades performance for safety.
     * <p>
     * User is expected to avoid calling this method on recently (in same system) removed components
     * or invalid entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
     * or a partially recycled component if called on in-system removed components.
     * <p>
     * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
     * this method from within a subscription listener.
     *
     * @param e the entity that should possess the component
     * @return the instance of the component.
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public A get(Entity e) throws ArrayIndexOutOfBoundsException {
        return get(e.getId());
    }

    /**
     * Checks if the entity has this type of component.
     *
     * @param e the entity to check
     * @return true if the entity has this component type, false if it doesn't
     */
    @Override
    public boolean has(Entity e) throws ArrayIndexOutOfBoundsException {
        return has(e.getId());
    }

    /**
     * Create component for this entity.
     * Will avoid creation if component preexists.
     *
     * @param entity the entity that should possess the component
     * @return the instance of the component.
     */
    @Override
    public A create(Entity entity) {
        return create(entity.getId());
    }

    /**
     * Remove component from entity.
     * Does nothing if already removed.
     *
     * @param entity entity to remove.
     */
    @Override
    public void remove(Entity entity) {
        remove(entity.getId());
    }

    /**
     * Create or remove a component from an entity.
     * <p>
     * Does nothing if already removed or created respectively.
     *
     * @param entity Entity to change.
     * @param value  {@code true} to create component (if missing), {@code false} to remove (if exists).
     * @return the instance of the component, or {@code null} if removed.
     */
    @Override
    public A set(Entity entity, boolean value) {
        return set(entity.getId(), value);
    }

    /**
     * Fast and safe retrieval of a component for this entity.
     * If the entity does not have this component then fallback is returned.
     *
     * @param entity   Entity that should possess the component
     * @param fallback fallback component to return, or {@code null} to return null.
     * @return the instance of the component
     */
    @Override
    public A getSafe(Entity entity, A fallback) {
        return getSafe(entity.getId(), fallback);
    }
}
