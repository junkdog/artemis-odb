package com.artemis;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.artemis.annotations.Mapper;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;


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

	/** Mananges all entities for the world. */
	private final EntityManager em;
	/** Manages all component-entity associations for the world. */
	private final ComponentManager cm;
	/** The time passed since the last update. */
	public float delta;
	/** Entities added to the world since the last update. */
	private final WildBag<Entity> added;
	/** Entities deleted from the world since the last update. */
	private final Bag<Entity> deleted;

	/** Runs actions on systems and managers when entities get added. */
	private final AddedPerformer addedPerformer;
	/** Runs actions on systems and managers when entities are changed. */
	private final ChangedPerformer changedPerformer;
	/** Runs actions on systems and managers when entities are deleted. */
	private final DeletedPerformer deletedPerformer;
	/** Runs actions on systems and managers when entities are (re)enabled. */
	private final EnabledPerformer enabledPerformer;
	/** Runs actions on systems and managers when entities are disabled. */
	private final DisabledPerformer disabledPerformer;

	/** Contains all managers and managers classes mapped. */
	private final Map<Class<? extends Manager>, Manager> managers;
	/** Contains all managers unordered. */
	private final Bag<Manager> managersBag;
	/** Contains all systems and systems classes mapped. */
	private final Map<Class<?>, EntitySystem> systems;
	/** Contains all systems unordered. */
	private final Bag<EntitySystem> systemsBag;
	/** Contains all uninitilized systems. **/
	private final Bag<EntitySystem> systemsToInit;

	
	/**
	 * Creates a new world.
	 * <p>
	 * An EntityManager and ComponentManager are created and added upon
	 * creation.
	 * </p>
	 */
	public World() {
		this(64);
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
	public World(int expectedEntityCount) {
		managers = new IdentityHashMap<Class<? extends Manager>, Manager>();
		managersBag = new Bag<Manager>();
		
		systems = new IdentityHashMap<Class<?>, EntitySystem>();
		systemsBag = new Bag<EntitySystem>();
		systemsToInit = new Bag<EntitySystem>();

		added = new WildBag<Entity>();
		deleted = new Bag<Entity>();
		
		addedPerformer = new AddedPerformer();
		changedPerformer = new ChangedPerformer();
		deletedPerformer = new DeletedPerformer();
		enabledPerformer = new EnabledPerformer();
		disabledPerformer = new DisabledPerformer();

		cm = new ComponentManager(expectedEntityCount);
		setManager(cm);
		
		em = new EntityManager(expectedEntityCount);
		setManager(em);
	}

	
	/**
	 * Makes sure all managers systems are initialized in the order they were
	 * added.
	 */
	public void initialize() {
		for (int i = 0; i < managersBag.size(); i++) {
			managersBag.get(i).initialize();
		}
		
		initializeSystems();
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
	 * @param <T>
	 *			class type of the manager
	 * @param manager
	 *			manager to be added
	 *
	 * @return the manager
	 */
	public final <T extends Manager> T setManager(T manager) {
		managers.put(manager.getClass(), manager);
		managersBag.add(manager);
		manager.setWorld(this);
		
		ComponentMapperInitHelper.config(manager, this);
		
		return manager;
	}

	/**
	 * Returns a manager of the specified type.
	 * 
	 * @param <T>
	 *			class type of the manager
	 * @param managerType
	 *			class type of the manager
	 *
	 * @return the manager
	 */
	@SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerType) {
		return (T)managers.get(managerType);
	}
	
	/**
	 * Deletes the manager from this world.
	 *
	 * @param manager
	 *			manager to delete
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
	 * @param delta
	 *			time since last game loop
	 */
	public void setDelta(float delta) {
		this.delta = delta;
	}
	
	/**
	 * Adds a entity to this world.
	 * 
	 * @param e
	 *			the entity to add
	 */
	public void addEntity(Entity e) {
		added.add(e);
	}
	
	/**
	 * Ensure all systems are notified of changes to this entity.
	 * <p>
	 * If you're adding a component to an entity after it's been added to the
	 * world, then you need to invoke this method.
	 * </p>
	 * 
	 * @param e
	 *			the changed entity
	 */
	public void changedEntity(Entity e) {
		check(e, changedPerformer);
	}
	
	/**
	 * Delete the entity from the world.
	 * 
	 * @param e
	 *			the entity to delete
	 */
	public void deleteEntity(Entity e) {
		if (!deleted.contains(e)) {
			deleted.add(e);
			check(e, deletedPerformer);
		}
		
		if(added.contains(e)) {
			added.remove(e);
		}
	}

	/**
	 * (Re)enable the entity in the world, after it having being disabled.
	 * <p>
	 * Won't do anything unless it was already disabled.
	 * </p>
	 *
	 * @param e
	 *			the entity to enable
	 */
	public void enable(Entity e) {
		check(e, enabledPerformer);
	}

	/**
	 * Disable the entity from being processed.
	 * <p>
	 * Won't delete it, it will continue to exist but won't get processed.
	 * </p>
	 *
	 * @param e
	 *			the entity to disable
	 */
	public void disable(Entity e) {
		check(e, disabledPerformer);
	}

	/**
	 * Create and return a new or reused entity instance.
	 * <p>
	 * Will NOT add the entity to the world, use {@link #addEntity(Entity)} for
	 * that.
	 * </p>
	 * 
	 * @return entity
	 */
	public Entity createEntity() {
		return em.createEntityInstance();
	}
	
	/**
	 * Create and return a new or reused entity instance.
	 * <p>
	 * Will NOT add the entity to the world, use {@link #addEntity(Entity)} for
	 * that.
	 * </p>
	 * 
	 * @param uuid
	 *		the UUID to give to the entity
	 * 
	 * @return entity
	 */
	public Entity createEntity(UUID uuid) {
		Entity entity = em.createEntityInstance();
		entity.setUuid(uuid);
		return entity;
	}
	
	/**
	 * Get a entity having the specified id.
	 * 
	 * @param entityId
	 *			the entities id
	 *
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
	 * @param <T>
	 *			the system class type
	 * @param system
	 *			the system to add
	 *
	 * @return the added system
	 */
	public <T extends EntitySystem> T setSystem(T system) {
		return setSystem(system, false);
	}

	/**
	 * Will add a system to this world.
	 *  
	 * @param <T>
	 *			the system class type
	 * @param system
	 *			the system to add
	 * @param passive
	 *			whether or not this system will be processed by
	 *			{@link #process()}
	 *
	 * @return the added system
	 */
	public <T extends EntitySystem> T setSystem(T system, boolean passive) {
		system.setWorld(this);
		system.setPassive(passive);
		
		systems.put(system.getClass(), system);
		systemsBag.add(system);
		systemsToInit.add(system);
		
		ComponentMapperInitHelper.config(system, this);
		
		return system;
	}
	
	/**
	 * Remove the specified system from the world.
	 *
	 * @param system
	 *			the system to be deleted from world
	 */
	public void deleteSystem(EntitySystem system) {
		systems.remove(system.getClass());
		systemsBag.remove(system);
		systemsToInit.remove(system);
	}

	/**
	 * Run performers on all systems.
	 *
	 * @param performer
	 *			the performer to run
	 * @param e
	 *			the entity to pass as argument to the systems
	 */
	private void notifySystems(Performer performer, Entity e) {
		Object[] data = systemsBag.getData();
		for(int i = 0, s = systemsBag.size(); s > i; i++) {
			performer.perform((EntitySystem)data[i], e);
		}
	}

	/**
	 * Run performers on all managers.
	 *
	 * @param performer
	 *			the performer to run
	 * @param e
	 *			the entity to pass as argument to the managers
	 */
	private void notifyManagers(Performer performer, Entity e) {
		Object[] data = managersBag.getData();
		for(int i = 0, s = managersBag.size(); s > i; i++) {
			performer.perform((Manager)data[i], e);
		}
	}
	
	/**
	 * Retrieve a system for specified system type.
	 * 
	 * @param <T>
	 *			the class type of system
	 * @param type
	 *			type of system
	 *
	 * @return instance of the system in this world
	 */
	@SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> type) {
		return (T)systems.get(type);
	}
	
	/**
	 * Performs an action on each entity.
	 *
	 * @param entityBag
	 *			contains the entities upon which the action will be performed
	 * @param performer
	 *			the performer that carries out the action
	 */
	private void check(WildBag<Entity> entityBag, Performer performer) {
		Object[] entities = entityBag.getData();
		for (int i = 0, s = entityBag.size(); s > i; i++) {
			Entity e = (Entity)entities[i];
			entities[i] = null;
			notifyManagers(performer, e);
			notifySystems(performer, e);
		}
		entityBag.setSize(0);
	}

	/**
	 * Performs an action on an entity.
	 *
	 * @param e
	 *			the entity to use
	 * @param performer
	 *			the performer to run
	 */
	private void check(Entity e, Performer performer) {
		notifyManagers(performer, e);
		notifySystems(performer, e);
	}
	
	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		check(added, addedPerformer);
		deleted.clear();

		em.clean();
		cm.clean();
		
		// Some systems may add other systems in their initialize() method.
		// Initialize those newly added systems right after setSystem() call.
		if (systemsToInit.size() > 0) {
			initializeSystems();
		}
		
		Object[] systemsData = systemsBag.getData();
		for(int i = 0, s = systemsBag.size(); s > i; i++) {
			EntitySystem system = (EntitySystem)systemsData[i];
			if(!system.isPassive()) {
				system.process();
			}
		}
	}

	private void initializeSystems() {
		for (int i = 0, s = systemsToInit.size(); i < s; i++) {
			systemsToInit.get(i).initialize();
		}
		systemsToInit.clear();
	}
	
	/**
	 * Retrieves a ComponentMapper instance for fast retrieval of components
	 * from entities.
	 * 
	 * @param <T>
	 *			class type of the component
	 * @param type
	 *			type of component to get mapper for
	 *
	 * @return mapper for specified component type
	 */
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
		return BasicComponentMapper.getFor(type, this);
	}


	/** Runs {@link EntityObserver#deleted}. */
	private static final class DeletedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, Entity e) {
			observer.deleted(e);
		}

	}

	/** Runs {@link EntityObserver#enabled}. */
	private static final class EnabledPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, Entity e) {
			observer.enabled(e);
		}

	}

	/** Runs {@link EntityObserver#disabled}. */
	private static final class DisabledPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, Entity e) {
			observer.disabled(e);
		}

	}

	/** Runs {@link EntityObserver#changed}. */
	private static final class ChangedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, Entity e) {
			observer.changed(e);
		}

	}

	/** Runs {@link EntityObserver#added}. */
	private static final class AddedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, Entity e) {
			observer.added(e);
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
		 * @param observer
		 *			the observer with the method to calll
		 * @param e
		 *			the entity to pass as argument
		 */
		void perform(EntityObserver observer, Entity e);

	}


	/**
	 * Injects {@link BasicComponentMapper} instances into objects.
	 */
	private static final class ComponentMapperInitHelper {

		/**
		 * Injects a {@link BasicComponentMapper} instance for every {@link Mapper}
		 * annotation into the given system.
		 *
		 * @param target
		 *			the object to inject into (usually an {@link EntitySystem})
		 * @param world 
		 *			the world responsible for the component mappers
		 *
		 * @throws RuntimeException
		 */
		public static void config(Object target, World world) throws RuntimeException {
			try {
				Class<?> clazz = target.getClass();
				for (Field field : ClassReflection.getDeclaredFields(clazz)) {
					if (field.hasAnnotation(Mapper.class)) {
						@SuppressWarnings("unchecked")
						Class<Component> componentType = (Class<Component>)  field.getElementType(0);
						field.setAccessible(true);
						field.set(target, world.getMapper(componentType));
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error while setting component mappers", e);
			}
		}

	}

}
