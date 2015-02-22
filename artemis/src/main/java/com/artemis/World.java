package com.artemis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.EntityManager.NO_COMPONENTS;


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

	final WildBag<Entity> added;
	final WildBag<Entity> changed;
	final WildBag<Entity> disabled;
	final WildBag<Entity> enabled;
	final WildBag<Entity> deleted;

	/**
	 * Runs actions on systems and managers when entities get added.
	 */
	private final AddedPerformer addedPerformer;
	/**
	 * Runs actions on systems and managers when entities are changed.
	 */
	private final ChangedPerformer changedPerformer;
	/**
	 * Runs actions on systems and managers when entities are deleted.
	 */
	private final DeletedPerformer deletedPerformer;
	/**
	 * Runs actions on systems and managers when entities are (re)enabled.
	 */
	private final EnabledPerformer enabledPerformer;
	/**
	 * Runs actions on systems and managers when entities are disabled.
	 */
	private final DisabledPerformer disabledPerformer;

	/**
	 * Contains all managers and managers classes mapped.
	 */
	private final Map<Class<? extends Manager>, Manager> managers;
	/**
	 * Contains all managers unordered.
	 */
	private final Bag<Manager> managersBag;
	/**
	 * Contains all systems and systems classes mapped.
	 */
	private final Map<Class<?>, BaseSystem> systems;
	/**
	 * Contains all systems unordered.
	 */
	private final Bag<BaseSystem> systemsBag;

	private boolean registerUuids;
	private Injector injector;
	
	final EntityEditPool editPool = new EntityEditPool(this);
	
	private boolean initialized;

	private SystemInvocationStrategy invocationStrategy;
	
	/**
	 * Creates a new world.
	 * <p>
	 * An EntityManager and ComponentManager are created and added upon
	 * creation.
	 * </p>
	 */
	public World() {
		this(new WorldConfiguration().maxRebuiltIndicesPerTick(64));
	}

	/**
	 * @deprecated {@link World#World(WorldConfiguration)} provides more fine-grained control.
	 */
	@Deprecated
	public World(int expectedEntityCount) {
		this(new WorldConfiguration().maxRebuiltIndicesPerTick(expectedEntityCount));
	}
	
	/**
	 * Creates a new world.
	 * <p>
	 * An EntityManager and ComponentManager are created and added upon
	 * creation.
	 * </p>
	 */
	public World(WorldConfiguration configuration) {
		managers = new IdentityHashMap<Class<? extends Manager>, Manager>();
		managersBag = new Bag<Manager>();

		systems = new IdentityHashMap<Class<?>, BaseSystem>();
		systemsBag = new Bag<BaseSystem>();

		added = new WildBag<Entity>();
		changed = new WildBag<Entity>();
		deleted = new WildBag<Entity>();
		enabled = new WildBag<Entity>();
		disabled = new WildBag<Entity>();

		addedPerformer = new AddedPerformer();
		changedPerformer = new ChangedPerformer();
		deletedPerformer = new DeletedPerformer();
		enabledPerformer = new EnabledPerformer();
		disabledPerformer = new DisabledPerformer();

		cm = setManager(new ComponentManager(configuration.expectedEntityCount()));
		em = setManager(new EntityManager(configuration.expectedEntityCount()));
		am = setManager(new AspectSubscriptionManager());

		injector = new Injector(this, configuration);
	}
	
	/**
	 * Makes sure all managers systems are initialized in the order they were
	 * added.
	 */
	public void initialize() {
		initialized = true;
		injector.update();
		for (int i = 0; i < managersBag.size(); i++) {
			Manager manager = managersBag.get(i);
			injector.inject(manager);
			manager.initialize();
		}

		initializeSystems();

		if (invocationStrategy == null)
			setInvocationStrategy(new InvocationStrategy());
	}

	/**
	 * Inject dependencies on object.
	 *
	 * Immediately perform dependency injection on the target.
	 * {@link com.artemis.annotations.Wire} annotation is required on the target
	 * or fields.
	 *
	 * If you want to specify nonstandard dependencies to inject, use
	 * {@link com.artemis.WorldConfiguration#register(String, Object)} instead.
	 *
	 * @see com.artemis.annotations.Wire for more details about dependency injection.
	 * @param target Object to inject into.
	 */
	public void inject(Object target) {
		assertInitialized();
		if (!ClassReflection.isAnnotationPresent(target.getClass(), Wire.class))
			throw new MundaneWireException(target.getClass().getName() + " must be annotated with @Wire");

		injector.inject(target);
	}

	private void assertInitialized() {
		if (!initialized)
			throw new MundaneWireException("World#initialize() has not yet been called.");
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
		
		assertInitialized();
		
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
	 */
	public final <T extends Manager> T setManager(T manager) {
		if (initialized) {
			String err = "It is forbidden to add managers after calling World#initialized.";
			throw new RuntimeException(err);
		}

		managers.put(manager.getClass(), manager);
		managersBag.add(manager);
		manager.setWorld(this);
		
		if (manager instanceof UuidEntityManager)
			registerUuids = true;

		return manager;
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
		if (disabled.contains(e))
			disabled.remove(e);
		
		enabled.add(e);
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
		if (enabled.contains(e))
			enabled.remove(e);
		
		disabled.add(e);
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
		added.add(e);
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
	 */
	public <T extends BaseSystem> T setSystem(T system) {
		return setSystem(system, false);
	}

	/**
	 * Will add a system to this world.
	 *
	 * @param <T>	 the system class type
	 * @param system  the system to add
	 * @param passive whether or not this system will be processed by
	 *				{@link #process()}
	 * @return the added system
	 */
	public <T extends BaseSystem> T setSystem(T system, boolean passive) {

		if (initialized) {
			String err = "It is forbidden to add systems after calling World#initialized.";
			throw new RuntimeException(err);
		}

		system.setPassive(passive);
		system.setWorld(this);

		systems.put(system.getClass(), system);
		systemsBag.add(system);

		return system;
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
	 * Run performers on all managers.
	 *
	 * @param performer the performer to run
	 * @param entities the entity to pass as argument to the managers
	 */
	private void notifyManagers(Performer performer, WildBag<Entity> entities) {
		Object[] data = managersBag.getData();
		for (int i = 0, s = managersBag.size(); s > i; i++) {
			performer.perform((Manager) data[i], entities);
		}
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
		return (T) systems.get(type);
	}

	/**
	 * Performs an action on each entity.
	 *
	 * @param entityBag contains the entities upon which the action will be performed
	 * @param performer the performer that carries out the action
	 */
	private void check(WildBag<Entity> entityBag, Performer performer) {
		if (entityBag.size() == 0)
			return;
		
		notifyManagers(performer, entityBag);
	}

	public void setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
		this.invocationStrategy = invocationStrategy;
		invocationStrategy.setWorld(this);
	}

	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		assertInitialized();

		updateEntityStates();

		em.clean();
		cm.clean();

		invocationStrategy.process(systemsBag);
	}

	void updateEntityStates() {
		// the first block is for entities with precalculated compositionIds,
		// such as those affected by EntityTransmuters, Archetypes
		// and EntityFactories.
		while (added.size() > 0 || changed.size() > 0) {
			check(added, addedPerformer);
			check(changed, changedPerformer);

			am.process(added, changed, deleted);
		}
		
		while(editPool.processEntities()) {
			check(added, addedPerformer);
			check(changed, changedPerformer);
			check(deleted, deletedPerformer);
			check(disabled, disabledPerformer);
			check(enabled, enabledPerformer);

			am.process(added, changed, deleted);

			// we're cheating here to support disabled and enabled entities with the
			// new subscription lists
			// @deprecate
			am.process(added, enabled, disabled);
		}
	}

	private void initializeSystems() {
		for (int i = 0, s = systemsBag.size(); i < s; i++) {
			BaseSystem system = systemsBag.get(i);
			injector.inject(system);
			system.initialize();
		}
		am.processComponentIdentity(NO_COMPONENTS, new BitSet());
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
	 * Runs {@link EntityObserver#deleted}.
	 */
	private static final class DeletedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, WildBag<Entity> entities) {
			observer.deleted(entities);
		}
	}

	/**
	 * Runs {@link EntityObserver#enabled}.
	 */
	private static final class EnabledPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, WildBag<Entity> entities) {
			Object[] data = entities.getData();
			for (int i = 0, s = entities.size(); s > i; i++) {
				observer.enabled((Entity)data[i]);
			}
		}
	}

	/**
	 * Runs {@link EntityObserver#disabled}.
	 */
	private static final class DisabledPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, WildBag<Entity> entities) {
			Object[] data = entities.getData();
			for (int i = 0, s = entities.size(); s > i; i++) {
				observer.disabled((Entity)data[i]);
			}
		}
	}

	/**
	 * Runs {@link EntityObserver#changed}.
	 */
	private static final class ChangedPerformer implements Performer {
		
		@Override
		public void perform(EntityObserver observer, WildBag<Entity> entities) {
			observer.changed(entities);
		}
	}

	/**
	 * Runs {@link EntityObserver#added}.
	 */
	private static final class AddedPerformer implements Performer {
		
		@Override
		public void perform(EntityObserver observer, WildBag<Entity> entities) {
			observer.added(entities);
		}
	}

	/**
	 * Calls methods on observers.
	 * <p>
	 * Only used internally to maintain clean code.
	 * </p>
	 */
	private interface Performer {

		/**
		 * Call a method on the observer with the entity as argument.
		 *
		 * @param observer the observer with the method to calll
		 * @param entities	the entities to pass as argument
		 */
		void perform(EntityObserver observer, WildBag<Entity> entities);
	}

}
