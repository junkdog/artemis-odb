package com.artemis;

import com.artemis.ComponentType.Taxonomy;

import java.util.BitSet;

/**
 * Entity mutator.
 * <p/>
 * Provides a fast albeit verbose way to perform batch changes to entities.
 * <p/>
 * {@see com.artemis.BaseSystem}, {@see com.artemis.EntitySubscription.SubscriptionListener}
 * and {@see com.artemis.EntityObserver} are informed of changes only after the current
 * system has done processing and the next system is about to be invoked. This removes the
 * need for systems to defend their subscription lists and allows for cleaner code and better
 * performance.
 * <p/>
 * Alternatives to edit entities.
 * <p/>
 * - {@see com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.
 * - {@see com.artemis.EntityTransmuterFactory} Fastest but rigid way of changing entity component compositions.
 * - {@see com.artemis.Archetype} Fastest, low level, no parameterized components.
 * - {@see com.artemis.EntityFactory} Fast, clean and convenient. For fixed composition entities. Requires some setup.
 * Best choice for parameterizing pooled components.
 * - {@see com.artemis.ComponentMapper} For discrete operations.
 */
public final class EntityEdit {

    Entity entity;
    private World world;
    boolean hasBeenAddedToWorld;
    final BitSet componentBits;
    boolean scheduledDeletion;

    EntityEdit(World world) {
        this.world = world;
        componentBits = new BitSet();
    }

    /**
     * Delete the entity from the world. The entity is considered to be
     * in a final state once invoked; adding or removing components from an
     * entity scheduled for deletion will likely throw exceptions.
     */
    public void deleteEntity() {
        scheduledDeletion = true;
    }

    public <T extends Component> T create(Class<T> componentKlazz) {
        ComponentManager componentManager = world.getComponentManager();
        T component = componentManager.create(entity, componentKlazz);

        ComponentTypeFactory tf = world.getComponentManager().typeFactory;
        ComponentType componentType = tf.getTypeFor(componentKlazz);
        componentBits.set(componentType.getIndex());

        return component;
    }

    /**
     * Add a component to this entity.
     *
     * @param component the component to add to this entity
     * @return this EntityEdit for chaining
     * @see {@link #create(Class)}
     */
    public EntityEdit add(Component component) {
        ComponentTypeFactory tf = world.getComponentManager().typeFactory;
        return add(component, tf.getTypeFor(component.getClass()));
    }

    /**
     * Faster adding of components into the entity.
     * <p>
     * Not necessary to use this, but in some cases you might need the extra
     * performance.
     * </p>
     *
     * @param component the component to add
     * @param type      the type of the component
     * @return this EntityEdit for chaining
     * @see #create(Class)
     */
    public EntityEdit add(Component component, ComponentType type) {
        if (type.getTaxonomy() != Taxonomy.BASIC) {
            throw new InvalidComponentException(component.getClass(),
                    "Use EntityEdit#create(Class<Component>) for adding non-basic component types");
        }
        world.getComponentManager().addComponent(entity, type, component);

        componentBits.set(type.getIndex());

        return this;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * Removes the component from this entity.
     *
     * @param component the component to remove from this entity.
     * @return this EntityEdit for chaining
     */
    public EntityEdit remove(Component component) {
        return remove(component.getClass());
    }

    /**
     * Faster removal of components from a entity.
     *
     * @param type the type of component to remove from this entity
     * @return this EntityEdit for chaining
     */
    public EntityEdit remove(ComponentType type) {
        if (componentBits.get(type.getIndex())) {
            world.getComponentManager().removeComponent(entity, type);
            componentBits.clear(type.getIndex());
        }
        return this;
    }

    /**
     * Remove component by its type.
     *
     * @param type the class type of component to remove from this entity
     * @return this EntityEdit for chaining
     */
    public EntityEdit remove(Class<? extends Component> type) {
        ComponentTypeFactory tf = world.getComponentManager().typeFactory;
        return remove(tf.getTypeFor(type));
    }

    @Override
    public String toString() {
        return "EntityEdit[" + entity.getId() + "]";
    }
}