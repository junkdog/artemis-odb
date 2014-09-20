package com.artemis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.artemis.ArchetypeBuilder.Archetype;
import com.artemis.annotations.Mapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;


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
	private final Map<Class<?>, EntitySystem> systems;
	/**
	 * Contains all systems unordered.
	 */
	private final Bag<EntitySystem> systemsBag;
	/**
	 * Contains all uninitialized systems. *
	 */
	private final Bag<EntitySystem> systemsToInit;
	
	private boolean registerUuids;
	private ArtemisInjector injector;
	
	int rebuiltIndices;
	private int maxRebuiltIndicesPerTick;

	final EntityEditPool editPool = new EntityEditPool(this);

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
	 *
	 * @param expectedEntityCount To avoid resizing entity containers needlessly.
	 */
	public World(WorldConfiguration configuration) {
		managers = new IdentityHashMap<Class<? extends Manager>, Manager>();
		managersBag = new Bag<Manager>();

		systems = new IdentityHashMap<Class<?>, EntitySystem>();
		systemsBag = new Bag<EntitySystem>();
		systemsToInit = new Bag<EntitySystem>();

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

		cm = new ComponentManager(configuration.expectedEntityCount());
		setManager(cm);

		em = new EntityManager(configuration.expectedEntityCount());
		setManager(em);
		
		maxRebuiltIndicesPerTick = configuration.maxRebuiltIndicesPerTick();
		injector = new ArtemisInjector(this, configuration);
	}
	
	/**
	 * Makes sure all managers systems are initialized in the order they were
	 * added.
	 */
	public void initialize() {
		injector.udpate();
		for (int i = 0; i < managersBag.size(); i++) {
			Manager manager = managersBag.get(i);
			injector.inject(manager);
			manager.initialize();
		}

		initializeSystems();
	}
	
	public void inject(Object obj) {
		if (injector == null)
			throw new MundaneWireException("World#initialize() has not been called.");
		
		injector.inject(obj);
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

		for (EntitySystem system : systemsBag) {
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
	 */
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
	 * 
	 * @deprecated Better invoke {@link Entity#deleteFromWorld()} or {@link EntityEdit#deleteEntity()}
	 */
	@Deprecated @SuppressWarnings("static-method")
	public void deleteEntity(Entity e) {
		e.edit().deleteEntity();
	}
	
	boolean isRebuildingIndexAllowed() {
		return maxRebuiltIndicesPerTick > rebuiltIndices;
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
	 * Create and return an {@link EntityEdit} wrapping a new or reused entity instance.
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
	public ImmutableBag<EntitySystem> getSystems() {
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
	public <T extends EntitySystem> T setSystem(T system) {
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
	public <T extends EntitySystem> T setSystem(T system, boolean passive) {
		system.setWorld(this);
		system.setPassive(passive);

		systems.put(system.getClass(), system);
		systemsBag.add(system);
		systemsToInit.add(system);

		return system;
	}

	/**
	 * Remove the specified system from the world.
	 *
	 * @param system the system to be deleted from world
	 */
	public void deleteSystem(EntitySystem system) {
		systems.remove(system.getClass());
		systemsBag.remove(system);
		systemsToInit.remove(system);
	}

	/**
	 * Run performers on all systems.
	 *
	 * @param performer the performer to run
	 * @param e		 the entity to pass as argument to the systems
	 */
	private void notifySystems(Performer performer, WildBag<Entity> entities) {
		Object[] data = systemsBag.getData();
		for (int i = 0, s = systemsBag.size(); s > i; i++) {
			performer.perform((EntitySystem) data[i], entities);
		}
	}

	/**
	 * Run performers on all managers.
	 *
	 * @param performer the performer to run
	 * @param e		 the entity to pass as argument to the managers
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
	public <T extends EntitySystem> T getSystem(Class<T> type) {
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
		notifySystems(performer, entityBag);
		entityBag.setSize(0);
	}
	
	void processComponentIdentity(int id, BitSet componentBits) {
		Object[] data = systemsBag.getData();
		for (int i = 0, s = systemsBag.size(); s > i; i++) {
			((EntitySystem)data[i]).processComponentIdentity(id, componentBits);
		}
	}

	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		rebuiltIndices = 0;
		
		updateEntityStates();

		em.clean();
		cm.clean();

		// Some systems may add other systems in their initialize() method.
		// Initialize those newly added systems right after setSystem() call.
		if (systemsToInit.size() > 0) {
			initializeSystems();
		}

		Object[] systemsData = systemsBag.getData();
		for (int i = 0, s = systemsBag.size(); s > i; i++) {
			updateEntityStates();
			
			EntitySystem system = (EntitySystem) systemsData[i];
			if (!system.isPassive()) {
				system.process();
			}
		}
	}

	private void updateEntityStates() {
		if (added.size() > 0) {
			check(added, addedPerformer);
		}
		
		while(editPool.processEntities()) {
			check(added, addedPerformer);
			check(changed, changedPerformer);
			check(deleted, deletedPerformer);
			check(disabled, disabledPerformer);
			check(enabled, enabledPerformer);
		}
	}

	private void initializeSystems() {
		for (int i = 0, s = systemsToInit.size(); i < s; i++) {
			EntitySystem es = systemsToInit.get(i);
			injector.inject(es);
			es.initialize();
		}
		systemsToInit.clear();
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
		 * @param e		the entity to pass as argument
		 */
		void perform(EntityObserver observer, WildBag<Entity> entities);
	}

	/**
	 * Injects {@link ComponentMapper}, {@link EntitySystem} and {@link Manager} types into systems and
	 * managers.
	 */
	private static final class ArtemisInjector {
		private final World world;
		
		private final Map<Class<?>, Class<?>> systems;
		private final Map<Class<?>, Class<?>> managers;
		private final Map<String, Object> pojos;
		
		ArtemisInjector(World world, WorldConfiguration config) {
			this.world = world;
			
			systems = new IdentityHashMap<Class<?>, Class<?>>();
			managers = new IdentityHashMap<Class<?>, Class<?>>();
			pojos = new HashMap<String, Object>(config.injectables);
		}
		
		void udpate() {
			for (EntitySystem es : world.getSystems()) {
				Class<?> origin = es.getClass();
				Class<?> clazz = origin;
				do {
					systems.put(clazz, origin);
				} while ((clazz = clazz.getSuperclass()) != Object.class);
			}
			
			for (Manager manager : world.managersBag) {
				Class<?> origin = manager.getClass();
				Class<?> clazz = origin;
				do {
					managers.put(clazz, origin);
				} while ((clazz = clazz.getSuperclass()) != Object.class);
			}
			
		}

		public void inject(Object target) throws RuntimeException {
			try {
				Class<?> clazz = target.getClass();

				if (ClassReflection.hasAnnotation(clazz, Wire.class)) {
					Wire wire = ClassReflection.getAnnotation(clazz, Wire.class);
					if (wire != null) {
						injectValidFields(target, clazz, wire.failOnNull(), wire.injectInherited());
					}
				} else {
					injectAnnotatedFields(target, clazz);
				}
			} catch (ReflectionException e) {
				throw new MundaneWireException("Error while wiring", e);
			}
		}

		private void injectValidFields(Object target, Class<?> clazz, boolean failOnNull, boolean injectInherited)
				throws ReflectionException {

			Field[] declaredFields = ClassReflection.getDeclaredFields(clazz);
			for (int i = 0, s = declaredFields.length; s > i; i++) {
				injectField(target, declaredFields[i], failOnNull);
			}

			// should bail earlier, but it's just one more round.
			while (injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
				injectValidFields(target, clazz, failOnNull, injectInherited);
			}
		}

		private void injectAnnotatedFields(Object target, Class<?> clazz)
			throws ReflectionException {

			injectClass(target, clazz);
		}

		@SuppressWarnings("deprecation")
		private void injectClass(Object target, Class<?> clazz) throws ReflectionException {
			Field[] declaredFields = ClassReflection.getDeclaredFields(clazz);
			for (int i = 0, s = declaredFields.length; s > i; i++) {
				Field field = declaredFields[i];
				if (field.hasAnnotation(Mapper.class) || field.hasAnnotation(Wire.class)) {
					injectField(target, field, field.hasAnnotation(Wire.class));
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void injectField(Object target, Field field, boolean failOnNotInjected)
			throws ReflectionException {

			field.setAccessible(true);

			Class<?> fieldType;
			try {
				fieldType = field.getType();
			} catch (RuntimeException ignore) {
				// Swallow exception caused by missing typedata on gwt platform.
				// @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for users failing to add systems/components to gwt reflection inclusion config.
				return;
			}

			if (ClassReflection.isAssignableFrom(ComponentMapper.class, fieldType)) {
				ComponentMapper<?> mapper = world.getMapper(field.getElementType(0));
				if (failOnNotInjected && mapper == null) {
					throw new MundaneWireException("ComponentMapper not found for " + fieldType);
				}
				field.set(target, mapper);
			} else if (ClassReflection.isAssignableFrom(EntitySystem.class, fieldType)) {
				EntitySystem system = world.getSystem((Class<EntitySystem>)systems.get(fieldType));
				if (failOnNotInjected && system == null) {
					throw new MundaneWireException("EntitySystem not found for " + fieldType);
				}
				field.set(target, system);
			} else if (ClassReflection.isAssignableFrom(Manager.class, fieldType)) {
				Manager manager = world.getManager((Class<Manager>)managers.get(fieldType));
				if (failOnNotInjected && manager == null) {
					throw new MundaneWireException("Manager not found for " + fieldType);
				}
				field.set(target, manager);
			} else if (field.hasAnnotation(Wire.class)) {
				final Wire wire = field.getAnnotation(Wire.class);
				String key = wire.name();
				if ("".equals(key))
					key = field.getType().getName();
				
				if (pojos.containsKey(key))
					field.set(target, pojos.get(key));
			}
		}
	}
}
