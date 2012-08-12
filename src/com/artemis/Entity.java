package com.artemis;

import java.util.Iterator;
import java.util.UUID;

import com.artemis.utils.Bag;

/**
 * The entity class. Cannot be instantiated outside the framework, you must
 * create new entities using World.
 * 
 * @author Arni Arent
 * 
 */
public final class Entity {
	private UUID uuid = UUID.randomUUID();

	private int id;
	private long typeBits;
	private long systemBits;

	private World world;
	private EntityManager entityManager;
	private ComponentManager componentManager;
	
	protected Entity(World world, int id) {
		this.world = world;
		this.id = id;
		this.entityManager = world.getEntityManager();
		this.componentManager = world.getComponentManager();
	}

	/**
	 * The internal id for this entity within the framework. No other entity
	 * will have the same ID, but ID's are however reused so another entity may
	 * acquire this ID if the previous entity was deleted.
	 * 
	 * @return id of the entity.
	 */
	public int getId() {
		return id;
	}

	protected long getTypeBits() {
		return typeBits;
	}

	protected void addTypeBit(long bit) {
		typeBits |= bit;
	}

	protected void removeTypeBit(long bit) {
		typeBits &= ~bit;
	}

	protected long getSystemBits() {
		return systemBits;
	}

	protected void addSystemBit(long bit) {
		systemBits |= bit;
	}

	protected void removeSystemBit(long bit) {
		systemBits &= ~bit;
	}

	protected void setSystemBits(long systemBits) {
		this.systemBits = systemBits;
	}

	protected void setTypeBits(long typeBits) {
		this.typeBits = typeBits;
	}

	protected void reset() {
		systemBits = 0;
		typeBits = 0;
	}

	@Override
	public String toString() {
		return "Entity[" + id + "]";
	}

	/**
	 * Add a component to this entity.
	 * 
	 * @param component to add to this entity
	 */
	public void addComponent(Component component) {
		addComponent(component, ComponentType.getTypeFor(component.getClass()));
	}
	
	/**
	 * Faster adding of components into the entity. Not neccessery to use this, but
	 * in some cases you might need the extra performance.
	 * 
	 * @param component the component to add
	 * @param type of the component
	 */
	public void addComponent(Component component, ComponentType type) {
		componentManager.addComponent(this, type, component);
	}

	/**
	 * Removes the component from this entity.
	 * 
	 * @param component to remove from this entity.
	 */
	public void removeComponent(Component component) {
		removeComponent(component.getClass());
	}

	/**
	 * Faster removal of components from a entity.
	 * 
	 * @param component to remove from this entity.
	 */
	public void removeComponent(ComponentType type) {
		componentManager.removeComponent(this, type);
	}
	
	/**
	 * Remove component by its type.
	 * @param type
	 */
	public void removeComponent(Class<? extends Component> type) {
		removeComponent(ComponentType.getTypeFor(type));
	}

	/**
	 * Checks if the entity has been deleted from somewhere.
	 * 
	 * @return if it's active.
	 */
	public boolean isActive() {
		return entityManager.isActive(id);
	}

	/**
	 * This is the preferred method to use when retrieving a component from a
	 * entity. It will provide good performance.
	 * 
	 * @param type
	 *            in order to retrieve the component fast you must provide a
	 *            ComponentType instance for the expected component.
	 * @return
	 */
	public Component getComponent(ComponentType type) {
		return componentManager.getComponent(this, type);
	}

	/**
	 * Slower retrieval of components from this entity. Minimize usage of this,
	 * but is fine to use e.g. when creating new entities and setting data in
	 * components.
	 * 
	 * @param <T>
	 *            the expected return component type.
	 * @param type
	 *            the expected return component type.
	 * @return component that matches, or null if none is found.
	 */
	public <T extends Component> T getComponent(Class<T> type) {
		return type.cast(getComponent(ComponentType.getTypeFor(type)));
	}

	/**
	 * Get all components belonging to this entity. WARNING. Use only for
	 * debugging purposes, it is dead slow.
	 * 
	 * @return all components of this entity.
	 */
	public Iterator<Component> getComponentsIterator() {
		return componentManager.getComponentsIteratorFor(this);
	}
	
	/**
	 * Returns a bag of all components this entity has.
	 * You need to reset the bag yourself if you intend to fill it more than once.
	 * 
	 * @param fillBag the bag to put the components into.
	 * @return the fillBag with the components in.
	 */
	public Bag<Component> getComponents(Bag<Component> fillBag) {
		return componentManager.getComponentsFor(this, fillBag);
	}

	/**
	 * Refresh all changes to components for this entity. After adding or
	 * removing components, you must call this method. It will update all
	 * relevant systems. It is typical to call this after adding components to a
	 * newly created entity.
	 */
	public void addToWorld() {
		world.addEntity(this);
	}
	
	/**
	 * This entity has changed, a component added or deleted.
	 */
	public void changedInWorld() {
		world.changedEntity(this);
	}

	/**
	 * Delete this entity from the world.
	 */
	public void deleteFromWorld() {
		world.deleteEntity(this);
	}
	
	public UUID getUuid() {
		return uuid;
	}

}
