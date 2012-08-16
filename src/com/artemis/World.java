package com.artemis;

import java.util.HashMap;
import java.util.Map;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

/**
 * The primary instance for the framework. It contains all the managers.
 * 
 * You must use this to create, delete and retrieve entities.
 * 
 * It is also important to set the delta each game loop iteration, and initialize before game loop.
 * 
 * @author Arni Arent
 * 
 */
public class World {
	private EntityManager em;
	private ComponentManager cm;

	private int delta;
	private float deltaFloat;
	private Bag<Entity> added;
	private Bag<Entity> changed;
	private Bag<Entity> deleted;

	private Map<Class<? extends Manager>, Manager> managers;
	private Bag<Manager> managersBag;
	
	private Map<Class<?>, EntitySystem> systems;
	private Bag<EntitySystem> systemsBag;

	public World() {
		managers = new HashMap<Class<? extends Manager>, Manager>();
		managersBag = new Bag<Manager>();
		
		systems = new HashMap<Class<?>, EntitySystem>();
		systemsBag = new Bag<EntitySystem>();

		added = new Bag<Entity>();
		changed = new Bag<Entity>();
		deleted = new Bag<Entity>();

		em = new EntityManager();
		setManager(em);
		
		cm = new ComponentManager();
		setManager(cm);
	}

	
	/**
	 * Makes sure all managers systems are initialized in the order they were added.
	 */
	public void initialize() {
		for (int i = 0; i < managersBag.size(); i++) {
			managersBag.get(i).initialize();
		}
		
		for (int i = 0; i < systemsBag.size(); i++) {
			systemsBag.get(i).initialize();
		}
	}
	
	
	/**
	 * Returns a manager that takes care of all the entities in the world.
	 * entities of this world.
	 * 
	 * @return entity manager.
	 */
	public EntityManager getEntityManager() {
		return em;
	}
	
	/**
	 * Returns a manager that takes care of all the components in the world.
	 * 
	 * @return component manager.
	 */
	public ComponentManager getComponentManager() {
		return cm;
	}
	
	
	

	/**
	 * Add a manager into this world. It can be retrieved later.
	 * World will notify this manager of changes to entity.
	 * 
	 * @param manager to be added
	 */
	public void setManager(Manager manager) {
		managers.put(manager.getClass(), manager);
		managersBag.add(manager);
		manager.setWorld(this);
	}

	/**
	 * Returns a manager of the specified type.
	 * 
	 * @param <T>
	 * @param managerType
	 *            class type of the manager
	 * @return the manager
	 */
	public <T extends Manager> T getManager(Class<T> managerType) {
		return managerType.cast(managers.get(managerType));
	}
	
	/**
	 * Deletes the manager from this world.
	 * @param manager to delete.
	 */
	public void deleteManager(Manager manager) {
		managers.remove(manager);
		managersBag.remove(manager);
	}

	
	
	
	/**
	 * Time since last game loop.
	 * 
	 * @return delta in milliseconds.
	 */
	public int getDelta() {
		return delta;
	}

	/**
	 * You must specify the delta for the game here.
	 * 
	 * @param delta time since last game loop.
	 */
	public void setDelta(int delta) {
		this.delta = delta;
	}
	

	/**
	 * Get the deltaFloat value.
	 * This is not return the same as getDelta(), it will only return
	 * the value that was set using setDeltaFloat(float), delta and deltaFloat are not the same.
	 * @return delta time as float value.
	 */
	public float getDeltaFloat() {
		return deltaFloat;
	}

	/**
	 * Sets the deltaFloat value.
	 * This is NOT the same as setDelta(int) method, delta and deltaFloat are not the same.
	 * @param deltaFloat delta time as float.
	 */
	public void setDeltaFloat(float deltaFloat) {
		this.deltaFloat = deltaFloat;
	}


	/**
	 * Adds a entity to this world.
	 * 
	 * @param e entity
	 */
	public void addEntity(Entity e) {
		added.add(e);
	}
	
	/**
	 * Ensure all systems are notified of changes to this entity.
	 * If you're adding a component to an entity after it's been
	 * added to the world, then you need to invoke this method.
	 * 
	 * @param e entity
	 */
	public void changedEntity(Entity e) {
		changed.add(e);
	}
	
	/**
	 * Delete the entity from the world.
	 * 
	 * @param e entity
	 */
	public void deleteEntity(Entity e) {
		if (!deleted.contains(e)) {
			deleted.add(e);
		}
	}



	/**
	 * Create and return a new or reused entity instance.
	 * Will NOT add the entity to the world, use World.addEntity(Entity) for that.
	 * 
	 * @return entity
	 */
	public Entity createEntity() {
		return em.createEntityInstance();
	}

	/**
	 * Get a entity having the specified id.
	 * 
	 * @param entityId
	 * @return entity
	 */
	public Entity getEntity(int entityId) {
		return em.getEntity(entityId);
	}

	


	/**
	 * Gives you all the systems in this world for possible iteration.
	 * 
	 * @return all entity systems in world.
	 */
	public ImmutableBag<EntitySystem> getSystems() {
		return systemsBag;
	}

	/**
	 * Adds a system to this world that will be processed by World.process()
	 * 
	 * @param system the system to add.
	 * @return the added system.
	 */
	public EntitySystem setSystem(EntitySystem system) {
		return setSystem(system, false);
	}

	/**
	 * Will add a system to this world.
	 *  
	 * @param system the system to add.
	 * @param passive wether or not this system will be processed by World.process()
	 * @return the added system.
	 */
	public EntitySystem setSystem(EntitySystem system, boolean passive) {
		system.setWorld(this);
		system.setPassive(passive);
		
		systems.put(system.getClass(), system);
		systemsBag.add(system);
		
		return system;
	}
	
	/**
	 * Removed the specified system from the world.
	 * @param system to be deleted from world.
	 */
	public void deleteSystem(EntitySystem system) {
		systems.remove(system.getClass());
		systemsBag.remove(system);
	}
	
	private void notifySystems(Entity e) {
		for(int i = 0, s=systemsBag.size(); s > i; i++) {
			systemsBag.get(i).check(e);
		}
	}
	
	/**
	 * Retrieve a system for specified system type.
	 * 
	 * @param type type of system.
	 * @return instance of the system in this world.
	 */
	public <T extends EntitySystem> T getSystem(Class<T> type) {
		return type.cast(systems.get(type));
	}

	
	/**
	 * Performs an action on each entity.
	 * @param entities
	 * @param performer
	 */
	private void check(Bag<Entity> entities, Performer performer) {
		if (!entities.isEmpty()) {
			for (int i = 0; entities.size() > i; i++) {
				for(int a = 0; managersBag.size() > a; a++) {
					Entity e = entities.get(i);
					performer.perform(managersBag.get(a), e);
					notifySystems(e);
				}
			}
			entities.clear();
		}
	}
	
	/**
	 * Process all non-passive systems.
	 */
	public void process() {
		check(added, new Performer() {
			@Override
			public void perform(Manager manager, Entity e) {
				manager.added(e);
			}
		});
		check(changed, new Performer() {
			@Override
			public void perform(Manager manager, Entity e) {
				manager.changed(e);
			}
		});
		check(deleted, new Performer() {
			@Override
			public void perform(Manager manager, Entity e) {
				manager.deleted(e);
			}
		});
		
		for(int i = 0; systemsBag.size() > i; i++) {
			EntitySystem system = systemsBag.get(i);
			if(!system.isPassive()) {
				system.process();
			}
		}
	}
	

	/**
	 * Retrieves a ComponentMapper instance for fast retrieval of components from entities.
	 * 
	 * @param type of component to get mapper for.
	 * @return mapper for specified component type.
	 */
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
		return ComponentMapper.getFor(type, this);
	}
	

	/*
	 * Only used internally to maintain clean code.
	 */
	private interface Performer {
		void perform(Manager manager, Entity e);
	}

}
