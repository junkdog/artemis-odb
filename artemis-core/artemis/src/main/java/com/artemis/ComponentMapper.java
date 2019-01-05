package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.utils.Bag;

import static com.artemis.utils.reflect.ClassReflection.isAnnotationPresent;

/**
 * Provide high performance component access and mutation from within a System.
 * <p>
 * This is the recommended way to mutate composition and access components.
 * Component Mappers are as fast as Transmuters.
 *
 * @param <A> Component type to map.
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public class ComponentMapper<A extends Component> extends BaseComponentMapper<A> {

    /**
     * Holds all components of given type in the world.
     */
    final Bag<A> components;

    private final EntityTransmuter createTransmuter;
    private final EntityTransmuter removeTransmuter;
    private final ComponentPool pool;
    private final ComponentRemover<A> purgatory;


    @SuppressWarnings("unchecked")
    public ComponentMapper(Class<A> type, World world) {
        super(world.getComponentManager().typeFactory.getTypeFor(type));
        components = new Bag<>(type);

        pool = (this.type.isPooled)
                ? new ComponentPool(type)
                : null;

        if (world.isAlwaysDelayComponentRemoval() || isAnnotationPresent(type, DelayedComponentRemoval.class))
            purgatory = new DelayedComponentRemover<>(components, pool, world.batchProcessor);
        else
            purgatory = new ImmediateComponentRemover<>(components, pool);

        createTransmuter = new EntityTransmuterFactory(world).add(type).build();
        removeTransmuter = new EntityTransmuterFactory(world).remove(type).build();
    }

    /**
     * Fast but unsafe retrieval of a component for this entity.
     * <p>
     * This method trades performance for safety!
     * <p>
     * Might return null, throw {@link ArrayIndexOutOfBoundsException} or a partially recycled
     * component if called on in-system removed components.
     * <p>
     * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
     * this method from within a subscription listener.
     *
     * @param entityId the entity that should possess the component
     * @return the instance of the component.
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public A get(int entityId) throws ArrayIndexOutOfBoundsException {
        return components.get(entityId);
    }

    /**
     * Checks if the entity has this type of component.
     * <p>
     * Removed components flagged with {@link DelayedComponentRemoval} will report {@code false}
     * immediately after removal but will still be available using {@link #get(int)}.
     *
     * @param entityId the id of entity to check
     * @return {@code true} if the entity has this component type, {@code false} if it doesn't (or if it is scheduled for delayed removal).
     */
    @Override
    public boolean has(int entityId) {
        return get(entityId) != null && !purgatory.has(entityId);
    }


    /**
     * Schedule component for removal.
     * <p>
     * Components annotated with {@link DelayedComponentRemoval} (or all components if {@link World#isAlwaysDelayComponentRemoval}
     * mode is active) are removed after the system finishes processing and all subscribers have been called. However,
     * {@link #has} will immediately return {@code false} as if it was removed!
     * <p>
     * Non-delayed components are removed immediately. The component reference becomes invalid and should be discarded.
     * Immediate following calls to {@link #has} will return false, {@link #get} will return null.
     * AspectSubscriptions will reflect the changes only AFTER the system finishes processing.
     * <p>
     * Does nothing if already removed.
     *
     * @param entityId
     */
    @Override
    public void remove(int entityId) {
        A component = get(entityId);
        if (component != null) {
            removeTransmuter.transmuteNoOperation(entityId);
            purgatory.mark(entityId);
        }
    }

    @Override
    protected void internalRemove(int entityId) { // triggers no composition id update
        A component = get(entityId);
        if (component != null)
            purgatory.mark(entityId);
    }

    /**
     * Create component for this entity.
     * Avoids creation if component exists.
     *
     * @param entityId the entity that should possess the component
     * @return the instance of the component.
     */
    @Override
    public A create(int entityId) {
        A component = get(entityId);
        if (component == null || purgatory.unmark(entityId)) {
            // running transmuter first, as it performs som validation
            createTransmuter.transmuteNoOperation(entityId);
            component = createNew();
            components.unsafeSet(entityId, component);
        }

        return component;
    }

    @Override
    public A internalCreate(int entityId) {
        A component = get(entityId);
        if (component == null || purgatory.unmark(entityId)) {
            component = createNew();
            components.unsafeSet(entityId, component);
        }

        return component;
    }

    private A createNew() {
        return (A) ((pool != null)
                ? pool.obtain()
                : ComponentManager.newInstance(type.getType()));
    }

}
