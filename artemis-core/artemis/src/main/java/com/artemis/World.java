package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.injection.CachedInjector;
import com.artemis.injection.Injector;
import com.artemis.link.*;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;

import java.util.*;

import static com.artemis.WorldConfiguration.ASPECT_SUBSCRIPTION_MANAGER_IDX;
import static com.artemis.WorldConfiguration.COMPONENT_MANAGER_IDX;
import static com.artemis.WorldConfiguration.ENTITY_MANAGER_IDX;

/**
 * The primary instance for the framework.
 *
 * World is an isolated container for your entities, systems and components.
 *
 * Upon world instantiation, all systems initialized and injected.
 * Instance entities and assets via your systems and managers.
 *
 * Call the following piece of code each frame to run the world.
 *  {@code world.setDelta(delta); world.process(); }
 *
 * This instance exclusively references entities by int. If you want to
 * reference entities by {@see Entity} use {@see com.artemis.EntityWorld}, or if
 * you want to implement your own Entity extend {@see com.artemis.CosplayWorld}.
 *
 * @author Arni Arent
 * @author junkdog
 */
@SkipWire
public class World {

    /**
     * Manages all entities for the world.
     */
    protected final EntityManager em;

    /**
     * Manages all component-entity associations for the world.
     */
    protected final ComponentManager cm;

    /**
     * Pool of entity edits.
     */
    final BatchChangeProcessor batchProcessor;

    /**
     * Contains all systems unordered.
     */
    final Bag<BaseSystem> systemsBag;
    /**
     * Manages all aspect based entity subscriptions for the world.
     */
    final AspectSubscriptionManager asm;

    /**
     * Contains strategy for invoking systems upon process.
     */
    SystemInvocationStrategy invocationStrategy;

    final WorldSegment partition;

    /**
     * The time passed since the last update.
     */
    public float delta;
    private LinkFactory.ReflexiveMutators reflextiveMutators;
    private Class entityClass;

    /**
     * Creates a world without custom systems.
     * <p>
     * {@link com.artemis.EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
     * available by default.
     * </p>
     * @Deprecated Use {@link #World(WorldConfiguration)} to create a world with your own systems.
     */
    @Deprecated
    public World() {
        this(new WorldConfiguration());
    }

    /**
     * Creates a new world.
     * <p>
     * {@link com.artemis.EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
     * available by default, on top of your own systems.
     * </p>
     *
     * @see WorldConfigurationBuilder
     * @see WorldConfiguration
     */
    public World(WorldConfiguration configuration) {
        partition = new WorldSegment(configuration);
        systemsBag = configuration.systems;

        if (configuration.getComponentMapperFactory() == null) {
            configuration.setComponentMapperFactory(createBasicComponentMapperFactory());
        }

        final ComponentManager lcm =
                (ComponentManager) systemsBag.get(COMPONENT_MANAGER_IDX);
        final EntityManager lem =
                (EntityManager) systemsBag.get(ENTITY_MANAGER_IDX);
        final AspectSubscriptionManager lasm =
                (AspectSubscriptionManager) systemsBag.get(ASPECT_SUBSCRIPTION_MANAGER_IDX);

        cm = lcm == null ? new ComponentManager(configuration.expectedEntityCount(), configuration.getComponentMapperFactory()) : lcm;
        em = lem == null ? new EntityManager(configuration.expectedEntityCount()) : lem;
        asm = lasm == null ? new AspectSubscriptionManager() : lasm;
        batchProcessor = new BatchChangeProcessor(this);

        configuration.initialize(this, partition.injector, asm);
    }

    private ComponentMapperFactory createBasicComponentMapperFactory() {
        return new ComponentMapperFactory() {
            @Override
            public ComponentMapper instance(Class<? extends Component> type, World world) {
                return new ComponentMapper(type, world);
            }
        };
    }

    /**
     * Inject dependencies on object.
     * <p/>
     * Immediately perform dependency injection on the target, even if the target isn't of an Artemis class.
     * <p/>
     * If you want to specify nonstandard dependencies to inject, use
     * {@link com.artemis.WorldConfiguration#register(String, Object)} instead, or
     * configure an {@link com.artemis.injection.Injector}
     * <p/>
     * If you want a non-throwing alternative, use {@link #inject(Object, boolean)}
     *
     * @param target Object to inject into.
     *               throws {@link MundaneWireException} if {@code target} is annotated with {@link com.artemis.annotations.SkipWire}
     * @see com.artemis.annotations.Wire for more details about dependency injection.
     * @see #inject(Object, boolean)
     */
    public void inject(Object target) {
        inject(target, true);
    }

    /**
     * Inject dependencies on object.
     * <p/>
     * Will not if it is annotated with {@link com.artemis.annotations.Wire}.
     * <p/>
     * If you want to specify nonstandard dependencies to inject, use
     * {@link com.artemis.WorldConfiguration#register(String, Object)} instead, or
     * configure an {@link com.artemis.injection.Injector}.
     *
     * @param target              Object to inject into.
     * @param failIfNotInjectable if true, this method will
     *                            throws {@link MundaneWireException} if {@code target} is annotated with
     *                            {@link com.artemis.annotations.SkipWire} and {@code failIfNotInjectable} is true
     * @see com.artemis.annotations.Wire for more details about dependency injection.
     * @see #inject(Object)
     */
    public void inject(Object target, boolean failIfNotInjectable) {
        boolean injectable = partition.injector.isInjectable(target);
        if (!injectable && failIfNotInjectable)
            throw new MundaneWireException("Attempted injection on " + target.getClass()
                    .getName() + ", which is annotated with @SkipWire");

        if (injectable)
            partition.injector.inject(target);
    }

    public <T> T getRegistered(String name) {
        return partition.injector.getRegistered(name);
    }

    public <T> T getRegistered(Class<T> type) {
        return partition.injector.getRegistered(type);
    }

    /**
     * Disposes all systems. Only necessary if either need to free
     * managed resources upon bringing the world to an end.
     *
     * @throws ArtemisMultiException if any system throws an exception.
     */
    public void dispose() {
        List<Throwable> exceptions = new ArrayList<Throwable>();

        for (BaseSystem system : systemsBag) {
            try {
                system.dispose();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }

        if (exceptions.size() > 0)
            throw new ArtemisMultiException(exceptions);
    }

    /**
     * Get entity editor for entity.
     *
     * @param entityId entity to fetch editor for.
     * @return a fast albeit verbose editor to perform batch changes to entities.
     */
    public EntityEdit edit(int entityId) {
        if (!em.isActive(entityId))
            throw new RuntimeException("Issued edit on deleted " + entityId);

        return batchProcessor.obtainEditor(entityId);
    }

    /**
     * Gets the <code>composition id</code> uniquely identifying the
     * component composition of an entity. Each composition identity maps
     * to one unique <code>BitVector</code>.
     *
     * @param entityId Entity for which to get the composition id
     * @return composition identity of entity
     */
    public int compositionId(int entityId) {
        return cm.getIdentity(entityId);
    }

    /**
     * Returns a manager that takes care of all the entities in the world.
     *
     * @return entity manager
     */
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a manager that takes care of all the components in the world.
     *
     * @return component manager
     */
    public ComponentManager getComponentManager() {
        return cm;
    }

    /**
     * Returns the manager responsible for creating and maintaining
     * {@link EntitySubscription subscriptions} in the world.
     *
     * @return aspect subscription manager
     */
    public AspectSubscriptionManager getAspectSubscriptionManager() {
        return asm;
    }

    /**
     * Time since last game loop.
     *
     * @return delta time since last game loop
     */
    public float getDelta() {
        return delta;
    }

    /**
     * You must specify the delta for the game here.
     *
     * @param delta time since last game loop
     */
    public void setDelta(float delta) {
        this.delta = delta;
    }

    /**
     * Delete the entity from the world.
     * <p>
     * The entity is considered to be in a final state once invoked;
     * adding or removing components from an entity scheduled for
     * deletion will likely throw exceptions.
     *
     * @param entityId the entity to delete
     */
    public void delete(int entityId) {
        batchProcessor.delete(entityId);
    }

    /**
     * Create and return a new or reused entity id. Entity is
     * automatically added to the world.
     *
     * @return assigned entity id, where id >= 0.
     */
    public int create() {
        int entityId = em.create();
        batchProcessor.changed.unsafeSet(entityId);
        return entityId;
    }

    /**
     * Create and return a new or reused entity.
     * Entity is automatically added to the world.
     * <p>
     * Use {@link #edit(int)} to add components to your entity.
     * <p>
     * You can also create entities using:
     * - {@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.
     * - {@link com.artemis.Archetype} Fastest, low level, no parameterized components.
     *
     * @return assigned entity id
     */
    public int create(Archetype archetype) {
        int entityId = em.create();

        archetype.transmuter.perform(entityId);
        cm.setIdentity(entityId, archetype.compositionId);

        batchProcessor.changed.unsafeSet(entityId);

        return entityId;
    }

    /**
     * Gives you all the systems in this world for possible iteration.
     *
     * @return all entity systems in world
     */
    public ImmutableBag<BaseSystem> getSystems() {
        return systemsBag;
    }

    /**
     * Retrieve a system for specified system type.
     *
     * @param <T>  the class type of system
     * @param type type of system
     * @return instance of the system in this world
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return (T) partition.systems.get(type);
    }

    /**
     * Set strategy for invoking systems on {@link #process()}.
     */
    protected void setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
        this.invocationStrategy = invocationStrategy;
        invocationStrategy.setWorld(this);
        invocationStrategy.setSystems(systemsBag);
        invocationStrategy.initialize();
    }

    /**
     * Process all non-passive systems.
     *
     * @see InvocationStrategy to control and extend how systems are invoked.
     */
    public void process() {
        invocationStrategy.process();

        IntBag pendingPurge = batchProcessor.getPendingPurge();
        if (!pendingPurge.isEmpty()) {
            cm.clean(pendingPurge);
            em.clean(pendingPurge);

            batchProcessor.purgeComponents();
        }
    }

    /**
     * Retrieves a ComponentMapper instance for fast retrieval of components
     * from entities.
     * <p>
     * Odb automatically injects component mappers into systems, calling this
     * method is usually not required.,
     *
     * @param <T>  class type of the component
     * @param type type of component to get mapper for
     * @return mapper for specified component type
     */
    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        return cm.getMapper(type);
    }

    /**
     * @return Injector responsible for dependency injection.
     */
    public Injector getInjector() {
        return partition.injector;
    }

    /**
     * @return Strategy used for invoking systems during {@link World#process()}.
     */
    public <T extends SystemInvocationStrategy> T getInvocationStrategy() {
        return (T) invocationStrategy;
    }

    // TODO: refactor out of odb-core.
    public Class getEntityClass() {
        return entityClass;
    }

    public LinkFactory.ReflexiveMutators getReflextiveMutators() {

        class IntWorldReflexiveMutators implements LinkFactory.ReflexiveMutators {
            private final IntFieldMutator intField;
            private final IntBagFieldMutator intBagField;
            private final World world;

            private IntWorldReflexiveMutators(World world) {
                this.world = world;

                intField = new IntFieldMutator();
                intField.setWorld(world);

                intBagField = new IntBagFieldMutator();
                intBagField.setWorld(world);
            }

            public UniLinkSite withMutator(UniLinkSite linkSite) {
                if (linkSite.fieldMutator != null)
                    return linkSite;

                Class type = linkSite.field.getType();
                if (int.class == type) {
                    linkSite.fieldMutator = intField;
                } else {
                    throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
                }

                return linkSite;
            }

            public MultiLinkSite withMutator(MultiLinkSite linkSite) {
                if (linkSite.fieldMutator != null)
                    return linkSite;

                Class type = linkSite.field.getType();
                if (IntBag.class == type) {
                    linkSite.fieldMutator = intBagField;
                } else {
                    throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
                }

                return linkSite;
            }
        }

        return new IntWorldReflexiveMutators(this);
    }

    protected void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    static class WorldSegment {
        /**
         * Contains all systems and systems classes mapped.
         */
        final Map<Class<?>, BaseSystem> systems;

        /**
         * Responsible for dependency injection.
         */
        final Injector injector;

        WorldSegment(WorldConfiguration configuration) {
            systems = new IdentityHashMap<Class<?>, BaseSystem>();
            injector = (configuration.injector != null)
                    ? configuration.injector
                    : new CachedInjector();
        }
    }
}
