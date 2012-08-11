package com.artemis;

import com.artemis.utils.ImmutableBag;

/**
 * The entity class. Cannot be instantiated outside the framework, you must
 * create new entities using World.
 * 
 * @author Arni Arent
 * 
 */
public final class Entity {
	private int id;
	private long uniqueId;
	private long typeBits;
	private long systemBits;

	private World world;
	private EntityManager entityManager;

	protected Entity(World world, int id) {
		this.world = world;
		this.entityManager = world.getEntityManager();
		this.id = id;
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

	protected void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * Get the unique ID of this entity. Because entity instances are reused
	 * internally use this to identify between different instances.
	 * 
	 * @return the unique id of this entity.
	 */
	public long getUniqueId() {
		return uniqueId;
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
	 * @param component
	 *            to add to this entity
	 */
	public void addComponent(Component component) {
		entityManager.addComponent(this, component);
	}

	/**
	 * Removes the component from this entity.
	 * 
	 * @param component
	 *            to remove from this entity.
	 */
	public void removeComponent(Component component) {
		entityManager.removeComponent(this, component);
	}

	/**
	 * Faster removal of components from a entity.
	 * 
	 * @param component
	 *            to remove from this entity.
	 */
	public void removeComponent(ComponentType type) {
		entityManager.removeComponent(this, type);
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
		return entityManager.getComponent(this, type);
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
		return type.cast(getComponent(ComponentTypeManager.getTypeFor(type)));
	}

	/**
	 * Get all components belonging to this entity. WARNING. Use only for
	 * debugging purposes, it is dead slow. WARNING. The returned bag is only
	 * valid until this method is called again, then it is overwritten.
	 * 
	 * @return all components of this entity.
	 */
	public ImmutableBag<Component> getComponents() {
		return entityManager.getComponents(this);
	}

	/**
	 * Refresh all changes to components for this entity. After adding or
	 * removing components, you must call this method. It will update all
	 * relevant systems. It is typical to call this after adding components to a
	 * newly created entity.
	 */
	public void refresh() {
		world.refreshEntity(this);
	}

	/**
	 * Delete this entity from the world.
	 */
	public void delete() {
		world.deleteEntity(this);
	}

}
