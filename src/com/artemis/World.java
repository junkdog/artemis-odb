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
 * It is also important to set the delta each game loop iteration.
 * 
 * @author Arni Arent
 * 
 */
public class World {
	private EntityManager entityManager;

	private int delta;
	private Bag<Entity> refreshed;
	private Bag<Entity> deleted;

	private Map<Class<? extends Manager>, Manager> managers;
	private Bag<Manager> managersBag;
	
	private Map<Class<?>, EntitySystem> systems;
	private Bag<EntitySystem> systemsBag;

	public World() {
		managers = new HashMap<Class<? extends Manager>, Manager>();
		managersBag = new Bag<>();
		
		systems = new HashMap<>();
		systemsBag = new Bag<>();

		refreshed = new Bag<>();
		deleted = new Bag<>();

		entityManager = new EntityManager();

		setManager(entityManager);
	}

	/**
	 * Returns a manager that is the core of this framework, containing all the
	 * entities of this world.
	 * 
	 * @return entity manager.
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Add a manager into this world. It can be retrieved later.
	 * World will notify this manager of changes to entity.
	 * 
	 * @param manager
	 *            to be added
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
	 * @param delta
	 *            time since last game loop.
	 */
	public void setDelta(int delta) {
		this.delta = delta;
	}

	/**
	 * Delete the provided entity from the world.
	 * 
	 * @param e
	 *            entity
	 */
	public void deleteEntity(Entity e) {
		if (!deleted.contains(e)) {
			deleted.add(e);
		}
	}

	/**
	 * Ensure all systems are notified of changes to this entity.
	 * 
	 * @param e
	 *            entity
	 */
	public void refreshEntity(Entity e) {
		refreshed.add(e);
	}

	/**
	 * Create and return a new or reused entity instance.
	 * 
	 * @return entity
	 */
	public Entity createEntity() {
		return entityManager.create();
	}

	/**
	 * Get a entity having the specified id.
	 * 
	 * @param entityId
	 * @return entity
	 */
	public Entity getEntity(int entityId) {
		return entityManager.getEntity(entityId);
	}

	/**
	 * Let framework take care of internal business.
	 */
	public void loopStart() {
		updateRefreshed();
		updateDeleted();
	}

	private void updateDeleted() {
		if (!deleted.isEmpty()) {
			for (int i = 0; deleted.size() > i; i++) {
				Entity e = deleted.get(i);
				entityManager.remove(e);
				notifyManagersOfDeletedEntity(e);
			}
			deleted.clear();
		}
	}

	private void updateRefreshed() {
		if (!refreshed.isEmpty()) {
			for (int i = 0; refreshed.size() > i; i++) {
				Entity e = refreshed.get(i);
				entityManager.refresh(e);
				notifyManagersOfAddedEntity(e);
			}
			refreshed.clear();
		}
	}
	
	private void notifyManagersOfDeletedEntity(Entity e) {
		for(int i = 0; managersBag.size() > i; i++) {
			managersBag.get(i).removed(e);
		}
	}
	
	private void notifyManagersOfAddedEntity(Entity e) {
		for(int i = 0; managersBag.size() > i; i++) {
			managersBag.get(i).added(e);
		}
	}

	public void initialize() {
		for (int i = 0; i < systemsBag.size(); i++) {
			systemsBag.get(i).initialize();
		}
	}

	public ImmutableBag<EntitySystem> getSystems() {
		return systemsBag;
	}

	public EntitySystem setSystem(EntitySystem system) {
		system.setWorld(this);
		
		systems.put(system.getClass(), system);
		
		if(!systemsBag.contains(system))
			systemsBag.add(system);
		
		system.setSystemBit(SystemBitManager.getBitFor(system.getClass()));
		
		return system;
	}
	
	public <T extends EntitySystem> T getSystem(Class<T> type) {
		return type.cast(systems.get(type));
	}

}
