package com.artemis;

import com.artemis.injection.CachedInjector;
import com.artemis.injection.Injector;
import com.artemis.managers.UuidEntityManager;
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
 *
 * @author Arni Arent
 * @author junkdog
 */
public class World {

	/**
	 * Manages all entities for the world.
	 */
	private final EntityManager em;
	/**
	 * Manages all component-entity associations for the world.
	 */
	private final ComponentManager cm;
	private final AspectSubscriptionManager am;

	/**
	 * The time passed since the last update.
	 */
	public float delta;

	final BitSet added;
	final BitSet changed;
	final BitSet deleted;

	/**
	 * Contains all systems and systems classes mapped.
	 */
	final Map<Class<?>, BaseSystem> systems;

	/**
	 * Contains all entity observer implementing systems unordered.
	 */
	Bag<EntityObserver> entityObserversBag;

	/**
	 * Contains all systems unordered.
	 */
	private final Bag<BaseSystem> systemsBag;

	private boolean registerUuids;
	private Injector injector;
	
	final EntityEditPool editPool = new EntityEditPool(this);
	
	private SystemInvocationStrategy invocationStrategy;
	private WorldConfiguration configuration;

	/**
	 * Creates a new world.
	 * <p>
	 * An EntityManager and ComponentManager are created and added upon
	 * creation.
	 * </p>
	 */
	public World() {
		this(new WorldConfiguration());
	}

	/**
	 * @deprecated {@link World#World(WorldConfiguration)} provides more fine-grained control.
	 */
	@Deprecated
	public World(int expectedEntityCount) {
		this(new WorldConfiguration());
	}
	
	/**
	 * Creates a new world.
	 * <p>
	 * An EntityManager and ComponentManager are created and added upon
	 * creation.
	 * </p>
	 */
	public World(WorldConfiguration configuration) {
		this.configuration = configuration;

		systems = new IdentityHashMap<Class<?>, BaseSystem>();
		systemsBag = configuration.systems;

		added = new BitSet();
		changed = new BitSet();
		deleted = new BitSet();

		cm = new ComponentManager(configuration.expectedEntityCount());
		em = new EntityManager(configuration.expectedEntityCount());
		am = new AspectSubscriptionManager();
		injector = configuration.injector;
		if (injector == null) {
			injector = new CachedInjector();
		}

		configuration.initialize(this, injector, am);

		registerUuids = systems.get(UuidEntityManager.class) != null;
		if (invocationStrategy == null) {
			setInvocationStrategy(new InvocationStrategy());
		}

		collectEntityObservers();
	}

	private void collectEntityObservers() {
		entityObserversBag = new Bag<EntityObserver>();
		Object[] systemsData = systemsBag.getData();
		for (int i = 0, s = systemsBag.size(); s > i; i++) {
			final BaseSystem system = (BaseSystem) systemsData[i];
			if (ClassReflection.isAssignableFrom(EntityObserver.class, system.getClass()) ) {
				entityObserversBag.add((EntityObserver)system);
			}
		}
	}

	/**
	 * Makes sure all systems are initialized in the order they were
	 * added.
	 *
	 * @deprecated automatically covered by {@link WorldConfiguration}.
	 */
	@Deprecated
	public void initialize() {
	}

	/**
	 * Inject dependencies on object.
	 *
	 * Immediately perform dependency injection on the target.
	 * {@link com.artemis.annotations.Wire} annotation is required on the target
	 * or fields.
	 *
	 * If you want to specify nonstandard dependencies to inject, use
	 * {@link com.artemis.WorldConfiguration#register(String, Object)} instead, or
	 * configure an {@link com.artemis.injection.Injector}
	 *
	 * If you want a non-throwing alternative, use {@link #inject(Object, boolean)}
	 *
	 * @see com.artemis.annotations.Wire for more details about dependency injection.
	 * @see #inject(Object, boolean)
	 * @param target Object to inject into.
	 * throws MundaneWireException if {@code target} is not annotated with com.artemis.annotations.Wire
	 */
	public void inject(Object target) {
		inject(target, true);
	}

	/**
	 * Inject dependencies on object if it is annotated with {@link com.artemis.annotations.Wire}.
	 *
	 * If {@link com.artemis.annotations.Wire} is missing, no action will be taken.
	 *
	 * If you want to specify nonstandard dependencies to inject, use
	 * {@link com.artemis.WorldConfiguration#register(String, Object)} instead, or
	 * configure an {@link com.artemis.injection.Injector}
	 *
	 * @see com.artemis.annotations.Wire for more details about dependency injection.
	 * @see #inject(Object)
	 * @param target Object to inject into.
	 * @param failIfWireAnnotationIsMissing if true, this method will
	 * throws MundaneWireException if {@code target} is not annotated with com.artemis.annotations.Wire and
	 *        {@code failIfWireAnnotationIsMissing} is true
	 */
	public void inject(Object target, boolean failIfWireAnnotationIsMissing) {
		boolean injectable = injector.isInjectable(target);
		if (!injectable && failIfWireAnnotationIsMissing)
			throw new MundaneWireException(target.getClass().getName() + " must be annotated with @Wire");

		if(injectable)
			injector.inject(target);
	}


	/**
	 * Disposes all systems. Only necessary if either need to free
	 * managed resources upon bringing the world to an end.
	 *
	 * @throws ArtemisMultiException if any systems throws an exception.
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
	 * Returns a manager of the specified type.
	 *
	 * @param <T>		 class type of the manager
	 * @param managerType class type of the manager
	 * @return the manager
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseSystem> T getManager(Class<T> managerType) {
		return (T) systems.get(managerType);
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
	 * Adds a entity to this world.
	 *
	 * @param e the entity to add
	 * @deprecated internally managed by artemis
	 */
	@Deprecated
	public void addEntity(Entity e) {}

	/**
	 * @deprecated does nothing, internally tracked by artemis now.
	 */
	@Deprecated
	public void changedEntity(Entity e) {}

	/**
	 * Delete the entity from the world.
	 *
	 * @param e the entity to delete
	 */
	public void deleteEntity(Entity e) {
		e.edit().deleteEntity();
	}


	/**
	 * Delete the entity from the world.
	 *
	 * @param entityId the entity to delete
	 */
	public void deleteEntity(int entityId) {
		deleteEntity(em.getEntity(entityId));
	}
	

	/**
	 * Create and return a new or reused entity instance. Entity is 
	 * automatically added to the world.
	 *
	 * @return entity
	 */
	public Entity createEntity() {
		Entity e = em.createEntityInstance();
		e.edit();
		return e;
	}
	
	/**
	 * Create and return an {@link Entity} wrapping a new or reused entity instance.
	 * Entity is automatically added to the world.
	 *
	 * @return entity
	 */
	public Entity createEntity(Archetype archetype) {
		Entity e = em.createEntityInstance(archetype);
		cm.addComponents(e, archetype);
		added.set(e.id);
		return e;
	}
	
	/**
	 * Create and return a new or reused entity instance.
	 * <p>
	 * The uuid parameter is ignored if {@link UuidEntityManager} hasn't been added to the
	 * world. 
	 * </p>
	 *
	 * @param uuid the UUID to give to the entity
	 * @return entity
	 */
	public Entity createEntity(UUID uuid) {
		Entity entity = em.createEntityInstance();
		entity.setUuid(uuid);
		entity.edit();
		return entity;
	}

	/**
	 * Get a entity having the specified id.
	 *
	 * @param entityId the entities id
	 * @return the specific entity
	 */
	public Entity getEntity(int entityId) {
		return em.getEntity(entityId);
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
	 * Adds a system to this world that will be processed by
	 * {@link #process()}.
	 *
	 * @param <T>	the system class type
	 * @param system the system to add
	 * @return the added system
	 * * @deprecated {@link WorldConfiguration#setSystem(T)}
	 */
	@Deprecated
	public <T extends BaseSystem> T setSystem(T system) {
		throw new MundaneWireException("use WorldConfiguration#setSystem");
	}

	/**
	 * Will add a system to this world.
	 *
	 * @param <T>	 the system class type
	 * @param system  the system to add
	 * @param passive whether or not this system will be processed by
	 *				{@link #process()}
	 * @return the added system
	 * @deprecated {@link WorldConfiguration#setSystem(T, boolean)}
	 */
	@Deprecated
	public <T extends BaseSystem> T setSystem(T system, boolean passive) {
		throw new MundaneWireException("use WorldConfiguration#setSystem");
	}

	/**
	 * Remove the specified system from the world.
	 *
	 * @param system the system to be deleted from world
	 * @deprecated A world should be static once initialized
	 */
	@Deprecated
	public void deleteSystem(BaseSystem system) {}



	/**
	 * Retrieve a system for specified system type.
	 *
	 * @param <T>  the class type of system
	 * @param type type of system
	 * @return instance of the system in this world
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseSystem> T getSystem(Class<T> type) {
		return (T) systems.get(type);
	}

	/**
	 * Set strategy for invoking systems on {@see #process()}.
	 */
	protected void setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
		this.invocationStrategy = invocationStrategy;
		invocationStrategy.setWorld(this);
	}

	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		updateEntityStates();
		invocationStrategy.process(systemsBag);
	}

	void updateEntityStates() {
		// the first block is for entities with precalculated compositionIds,
		// such as those affected by EntityTransmuters, Archetypes
		// and EntityFactories.
		while (added.cardinality() > 0 || changed.cardinality() > 0) {
			am.process(added, changed, deleted);
		}
		
		while(editPool.processEntities()) {
			am.process(added, changed, deleted);
		}

		cm.clean();
	}

	boolean hasUuidManager() {
		return registerUuids;
	}

	/**
	 * Retrieves a ComponentMapper instance for fast retrieval of components
	 * from entities.
	 *
	 * @param <T>  class type of the component
	 * @param type type of component to get mapper for
	 * @return mapper for specified component type
	 */
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
		return BasicComponentMapper.getFor(type, this);
	}

	/**
	 * @return Injector used for dependency injection.
	 */
	public Injector getInjector() {
		return injector;
	}

	/**
	 * @return Strategy used for invoking systems during {@see World#process()}.
	 */
	public SystemInvocationStrategy getInvocationStrategy() {
		return invocationStrategy;
	}
}
