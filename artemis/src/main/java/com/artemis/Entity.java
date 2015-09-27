package com.artemis;

import com.artemis.utils.Bag;

import java.util.BitSet;


/**
 * The entity class.
 * <p>
 * Cannot be instantiated outside the framework, you must create new entities
 * using World. The world creates entities via it's entity manager.
 * </p>
 * @author Arni Arent
 */
public final class Entity {

	/** The entities identifier in the world. */
	int id;
	/** The world this entity belongs to. */
	private final World world;

	/**
	 * Creates a new {@link Entity} instance in the given world.
	 * <p>
	 * This will only be called by the world via it's entity manager,
	 * and not directly by the user, as the world handles creation of entities.
	 * </p>
	 * @param world
	 * 		the world to create the entity in
	 * @param id
	 * 		the id to set
	 */
	protected Entity(World world, int id) {
		this.world = world;
		this.id = id;
	}

	/**
	 * The internal id for this entity within the framework.
	 * <p>
	 * No other entity will have the same ID, but ID's are however reused so
	 * another entity may acquire this ID if the previous entity was deleted.
	 * </p>
	 * @return id of the entity
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns a BitSet instance containing bits of the components the entity
	 * possesses.
	 * @return a BitSet containing the entities component bits
	 */
	protected BitSet getComponentBits() {
		return world.getEntityManager().componentBits(id);
	}

	public EntityEdit edit() {
		return world.editPool.obtainEditor(id);
	}


	@Override
	public String toString() {
		return "Entity[" + id + "]";
	}

	/**
	 * Checks if the entity has been added to the world and has not been
	 * deleted from it.
	 * <p>
	 * If the entity has been disabled this will still return true.
	 * </p>
	 * @return {@code true} if it's active
	 */
	public boolean isActive() {
		return world.getEntityManager().isActive(id);
	}

	/**
	 * Retrieves component from this entity.
	 * <p>
	 * It will provide good performance. But the recommended way to retrieve
	 * components from an entity is using the ComponentMapper.
	 * </p>
	 * @param type
	 * 		in order to retrieve the component fast you must provide a
	 * 		ComponentType instance for the expected component
	 * @return
	 */
	public Component getComponent(ComponentType type) {
		return world.getComponentManager().getComponent(id, type);
	}

	/**
	 * Slower retrieval of components from this entity.
	 * <p>
	 * Minimize usage of this, but is fine to use e.g. when creating new
	 * entities and setting data in components.
	 * </p>
	 * @param <T>
	 * 		the expected return component class type
	 * @param type
	 * 		the expected return component class type
	 * @return component that matches, or null if none is found
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> type) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		return (T) getComponent(tf.getTypeFor(type));
	}

	/**
	 * Returns a bag of all components this entity has.
	 * <p>
	 * You need to reset the bag yourself if you intend to fill it more than
	 * once.
	 * </p>
	 * @param fillBag
	 * 		the bag to put the components into
	 * @return the fillBag containing the components
	 */
	public Bag<Component> getComponents(Bag<Component> fillBag) {
		return world.getComponentManager().getComponentsFor(id, fillBag);
	}

	/**
	 * Delete this entity from the world.
	 */
	public void deleteFromWorld() {
		edit().deleteEntity();
	}

	/**
	 * Returns the world this entity belongs to.
	 * @return world of entity
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return unique identifier for entities with this specific component configuration.
	 */
	public int getCompositionId() {
		return world.getEntityManager().getIdentity(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Entity entity = (Entity) o;

		return id == entity.id;

	}

	public boolean equals(Entity o) {
		return o != null && o.id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
