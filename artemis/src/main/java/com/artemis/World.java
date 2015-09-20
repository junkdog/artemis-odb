package com.artemis;

import com.artemis.injection.CachedInjector;
import com.artemis.injection.Injector;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.ReflectionException;

import java.util.*;

/**
 * The primary instance for the framework.
 * <p>
 * It contains all the systems. You must use this to create, delete and
 * retrieve entities. It is also important to set the delta each game loop
 * iteration, and initialize before game loop.
 * </p>
 * @author Arni Arent
 * @author junkdog
 */
public class World {

	/** Manages all entities for the world. */
	private final EntityManager em;

	/** Manages all component-entity associations for the world. */
	private final ComponentManager cm;

	/** Manages all aspect based entity subscriptions for the world. */
	private final AspectSubscriptionManager am;

	/** The time passed since the last update. */
	public float delta;

	final BitSet added;
	final BitSet changed;
	final BitSet deleted;

	/** Contains all systems and systems classes mapped. */
	final Map<Class<?>, BaseSystem> systems;

	/** Contains all entity observer implementing systems unordered. */
	Bag<EntityObserver> entityObserversBag;

	/** Contains all systems unordered. */
	private final Bag<BaseSystem> systemsBag;

	/** Responsible for dependency injection. */
	private Injector injector;

	/** Pool of entity edits. */
	final EntityEditPool editPool = new EntityEditPool(this);

	/** Contains strategy for invoking systems upon process. */
	private SystemInvocationStrategy invocationStrategy;

	/**
	 * Creates a world without custom systems.
	 * <p>
	 * {@link com.artemis.EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
	 * available by default.
	 * </p>
	 * Use {@link #World(WorldConfiguration)} to create a world with your own systems.
	 */
	public World() {
		this(new WorldConfiguration());
	}

	/**
	 * Creates a new world.
	 * <p>
	 * {@link com.artemis.EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
	 * available by default, on top of your own systems.
	 * </p>
	 * @see WorldConfiguration
	 * @see WorldConfigurationBuilder
	 */
	public World(WorldConfiguration configuration) {

		systems = new IdentityHashMap<Class<?>, BaseSystem>();
		systemsBag = configuration.systems;

		added = new BitSet();
		changed = new BitSet();
		deleted = new BitSet();

		final ComponentManager lcm = (ComponentManager) configuration.systems.get(WorldConfiguration.COMPONENT_MANAGER_IDX);
		final EntityManager lem = (EntityManager) configuration.systems.get(WorldConfiguration.ENTITY_MANAGER_IDX);
		final AspectSubscriptionManager lam = (AspectSubscriptionManager) configuration.systems.get(WorldConfiguration.ASPECT_SUBSCRIPTION_MANAGER_IDX);

		cm = lcm == null ? new ComponentManager(configuration.expectedEntityCount()) : lcm;
		em = lem == null ? new EntityManager(configuration.expectedEntityCount()) : lem;
		am = lam == null ? new AspectSubscriptionManager() : lam;

		injector = configuration.injector;
		if (injector == null) {
			injector = new CachedInjector();
		}

		configuration.initialize(this, injector, am);

		if (invocationStrategy == null) {
			setInvocationStrategy(new InvocationStrategy());
		}

		collectEntityObservers();
	}

	/** Create a bag of systems that implement {@link EntityObserver}. */
	private void collectEntityObservers() {
		entityObserversBag = new Bag<EntityObserver>();
		Object[] systemsData = systemsBag.getData();
		for (int i = 0, s = systemsBag.size(); s > i; i++) {
			final BaseSystem system = (BaseSystem) systemsData[i];
			if (ClassReflection.isAssignableFrom(EntityObserver.class, system.getClass())) {
				entityObserversBag.add((EntityObserver) system);
			}
		}
	}

	/**
	 * Inject dependencies on object.
	 * <p/>
	 * Immediately perform dependency injection on the target.
	 * <p/>
	 * If you want to specify nonstandard dependencies to inject, use
	 * {@link com.artemis.WorldConfiguration#register(String, Object)} instead, or
	 * configure an {@link com.artemis.injection.Injector}
	 * <p/>
	 * If you want a non-throwing alternative, use {@link #inject(Object, boolean)}
	 * @param target
	 * 		Object to inject into.
	 * 		throws {@link MundaneWireException} if {@code target} is annotated with {@link com.artemis.annotations.SkipWire}
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
	 * @param target
	 * 		Object to inject into.
	 * @param failIfNotInjectable
	 * 		if true, this method will
	 * 		throws {@link MundaneWireException} if {@code target} is annotated with
	 * 		{@link com.artemis.annotations.SkipWire} and {@code failIfNotInjectable} is true
	 * @see com.artemis.annotations.Wire for more details about dependency injection.
	 * @see #inject(Object)
	 */
	public void inject(Object target, boolean failIfNotInjectable) {
		boolean injectable = injector.isInjectable(target);
		if (!injectable && failIfNotInjectable)
			throw new MundaneWireException("Attempted injection on " + target.getClass()
					.getName() + ", which is annotated with @SkipWire");

		if (injectable)
			injector.inject(target);
	}

	/**
	 * Disposes all systems. Only necessary if either need to free
	 * managed resources upon bringing the world to an end.
	 * @throws ArtemisMultiException
	 * 		if any system throws an exception.
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
	 * Create entity factory for entities with a predefined component composition.
	 *
	 * @param factory type to create.
	 * @param <T> type to create. Should implement {@link EntityFactory}.
	 * @return Implementation of EntityFactory.
	 * @see EntityFactory for instructions about configuration.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityFactory> T createFactory(Class<?> factory) {
		if (!factory.isInterface())
			throw new MundaneWireException("Expected interface for type: " + factory);

		String impl = factory.getName() + "Impl";
		try {
			Class<?> implClass = ClassReflection.forName(impl);
			Constructor constructor = ClassReflection.getConstructor(implClass, World.class);
			return (T) constructor.newInstance(this);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a manager that takes care of all the entities in the world.
	 * @return entity manager
	 */
	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Returns a manager that takes care of all the components in the world.
	 * @return component manager
	 */
	public ComponentManager getComponentManager() {
		return cm;
	}

	/**
	 * Return a manager that takes care of all subscriptions in the world.
	 * @return aspect subscription manager
	 */
	public AspectSubscriptionManager getAspectSubscriptionManager() {
		return am;
	}

	/**
	 * Returns a manager of the specified type.
	 * @param <T>
	 * 		class type of the manager
	 * @param managerType
	 * 		class type of the manager
	 * @return the manager
	 * @deprecated managers and systems are treated equally. use {@link #getSystem(Class)} instead.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends BaseSystem> T getManager(Class<T> managerType) {
		return (T) systems.get(managerType);
	}

	/**
	 * Time since last game loop.
	 * @return delta time since last game loop
	 */
	public float getDelta() {
		return delta;
	}

	/**
	 * You must specify the delta for the game here.
	 * @param delta
	 * 		time since last game loop
	 */
	public void setDelta(float delta) {
		this.delta = delta;
	}

	/**
	 * Delete the entity from the world.
	 * @param e
	 * 		the entity to delete
	 */
	public void deleteEntity(int e) {
		EntityHelper.edit(this, e)
				.deleteEntity();
	}

	/**
	 * Create and return a new or reused entity instance. EntityHelper is
	 * automatically added to the world.
	 * @return entity
	 */
	public int createEntity() {
		int e = em.createEntityInstance();
		EntityHelper.edit(this, e);
		return e;
	}

	/**
	 * Create and return an {@link EntityHelper} wrapping a new or reused entity instance.
	 * EntityHelper is automatically added to the world.
	 *
	 * Use {@link EntityHelper#edit(World, int)} to set up your newly created entity.
	 *
	 * You can also create entities using:
	 * - {@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.
	 * - {@link com.artemis.Archetype} Fastest, low level, no parameterized components.
	 * - {@link com.artemis.EntityFactory} Fast, clean and convenient. For fixed composition entities. Requires some setup.
	 * Best choice for parameterizing pooled components.
	 *
	 * @return entity id
	 */
	public int createEntity(Archetype archetype) {
		int e = em.createEntityInstance(archetype);
		cm.addComponents(e, archetype);
		added.set(e);
		return e;
	}

	/**
	 * Get a entity having the specified id.
	 * @param entityId
	 * 		the entities id
	 * @return the specific entity
	 */
	public int getEntity(int entityId) {
		return em.getEntity(entityId);
	}

	/**
	 * Gives you all the systems in this world for possible iteration.
	 * @return all entity systems in world
	 */
	public ImmutableBag<BaseSystem> getSystems() {
		return systemsBag;
	}

	/**
	 * Retrieve a system for specified system type.
	 * @param <T>
	 * 		the class type of system
	 * @param type
	 * 		type of system
	 * @return instance of the system in this world
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseSystem> T getSystem(Class<T> type) {
		return (T) systems.get(type);
	}

	/** Set strategy for invoking systems on {@link #process()}. */
	protected void setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
		this.invocationStrategy = invocationStrategy;
		invocationStrategy.setWorld(this);
	}

	/** Process all non-passive systems.
	 * @see InvocationStrategy to control and extend how systems are invoked.
	 */
	public void process() {
		updateEntityStates();
		invocationStrategy.process(systemsBag);
	}

	/**
	 * Inform subscribers of state changes.
	 *
	 * Performs callbacks on systems implementing {@link EntityObserver} and afterwards any
	 * registered instances of {@link com.artemis.EntitySubscription.SubscriptionListener}.
	 *
	 * Will run repeatedly until any state changes caused by subscribers have been handled.
	 */
	void updateEntityStates() {
		// the first execution is for entities with precalculated compositionIds,
		// such as those affected by EntityTransmuters, Archetypes
		// and EntityFactories.
		do {
			am.process(added, changed, deleted);
		} while (editPool.processEntities());

		cm.clean();
	}

	/**
	 * Retrieves a ComponentMapper instance for fast retrieval of components
	 * from entities.
	 * @param <T>
	 * 		class type of the component
	 * @param type
	 * 		type of component to get mapper for
	 * @return mapper for specified component type
	 */
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
		return BasicComponentMapper.getFor(type, this);
	}

	/**
	 * @return Injector responsible for dependency injection.
	 */
	public Injector getInjector() {
		return injector;
	}

	/**
	 * @return Strategy used for invoking systems during {@link World#process()}.
	 */
	public SystemInvocationStrategy getInvocationStrategy() {
		return invocationStrategy;
	}
}
