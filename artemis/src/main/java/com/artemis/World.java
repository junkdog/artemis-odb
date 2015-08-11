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
 * It contains all the managers. You must use this to create, delete and
 * retrieve entities. It is also important to set the delta each game loop
 * iteration, and initialize before game loop.
 * </p>
 *
 * @author Arni Arent
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
	final BitSet disabled;
	final BitSet enabled;
	final BitSet deleted;



	/**
	 * Contains all managers and managers classes mapped.
	 */
	final Map<Class<? extends Manager>, Manager> managers;
	/**
	 * Contains all managers unordered.
	 */
	final Bag<Manager> managersBag;
	/**
	 * Contains all systems and systems classes mapped.
	 */
	final Map<Class<?>, BaseSystem> systems;
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
		managers = new IdentityHashMap<Class<? extends Manager>, Manager>();
		managersBag = configuration.managers;

		systems = new IdentityHashMap<Class<?>, BaseSystem>();
		systemsBag = configuration.systems;

		added = new BitSet();
		changed = new BitSet();
		deleted = new BitSet();
		enabled = new BitSet();
		disabled = new BitSet();

		cm = new ComponentManager(configuration.expectedEntityCount());
		em = new EntityManager(configuration.expectedEntityCount());
		am = new AspectSubscriptionManager();
		injector = configuration.injector;
		if (injector == null) {
			injector = new CachedInjector();
		}

		configuration.initialize(this, injector, am);

		registerUuids = managers.get(UuidEntityManager.class) != null;
		if (invocationStrategy == null)
			setInvocationStrategy(new InvocationStrategy());
	}

	/**
	 * Makes sure all managers systems are initialized in the order they were
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
	 * Disposes all managers and systems. Only necessary if either need to free
	 * managed resources upon bringing the world to an end.
	 *
	 * @throws ArtemisMultiException if any managers or systems throws an exception.
	 */
	public void dispose() {
		List<Throwable> exceptions = new ArrayList<Throwable>();

		for (Manager manager : managersBag) {
			try {
				manager.dispose();
			} catch (Exception e) {
				exceptions.add(e);
			}
		}

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
	 * Add a manager into this world.
	 * <p>
	 * It can be retrieved later. World will notify this manager of changes to
	 * entity.
	 * </p>
	 *
	 * @param <T>	 class type of the manager
	 * @param manager manager to be added
	 * @return the manager
	 *
	 * @deprecated {@link WorldConfiguration#setManager(Manager)}
	 */
	@Deprecated
	public final <T extends Manager> T setManager(T manager) {
		throw new MundaneWireException("use WorldConfiguration#setManager");
	}

	/**
	 * Returns a manager of the specified type.
	 *
	 * @param <T>		 class type of the manager
	 * @param managerType class type of the manager
	 * @return the manager
	 */
	@SuppressWarnings("unchecked")
	public <T extends Manager> T getManager(Class<T> managerType) {
		return (T) managers.get(managerType);
	}
	
	/**
	 * @return all managers in this world
	 */
	public ImmutableBag<Manager> getManagers() {
		return managersBag;
	}

	/**
	 * Deletes the manager from this world.
	 *
	 * @param manager manager to delete
	 * @deprecated A world should be static once initialized
	 */
	@Deprecated
	public void deleteManager(Manager manager) {
		managers.remove(manager.getClass());
		managersBag.remove(manager);
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
	 * (Re)enable the entity in the world, after it having being disabled.
	 * <p>
	 * Won't do anything unless it was already disabled.
	 * </p>
	 *
	 * @param e the entity to enable
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	public void enable(Entity e) {
		if (disabled.get(e.id))
			disabled.set(e.id, false);
		
		enabled.set(e.id, true);
	}

	/**
	 * Disable the entity from being processed.
	 * <p>
	 * Won't delete it, it will continue to exist but won't get processed.
	 * </p>
	 *
	 * @param e the entity to disable
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	public void disable(Entity e) {
		if (enabled.get(e.id))
			enabled.set(e.id, false);
		
		disabled.set(e.id);
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

	public void setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
		this.invocationStrategy = invocationStrategy;
		invocationStrategy.setWorld(this);
	}

	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		updateEntityStates();

		em.clean();
		cm.clean();

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

			// we're cheating here to support disabled and enabled entities with the
			// new subscription lists
			// @deprecate
			am.process(added, enabled, disabled);
		}
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
}
