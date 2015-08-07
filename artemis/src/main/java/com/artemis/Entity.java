package com.artemis;

import java.util.BitSet;
import java.util.UUID;

import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.Bag;


/**
 * The entity class.
 * <p>
 * Cannot be instantiated outside the framework, you must create new entities
 * using World. The world creates entities via it's entity manager.
 * </p>
 * 
 * @author Arni Arent
 */
public final class Entity {

	/** The entities identifier in the world. */
	public int id;
	/** The world this entity belongs to. */
	private final World world;

	/**
	 * Creates a new {@link Entity} instance in the given world.
	 * <p>
	 * This will only be called by the world via it's entity manager,
	 * and not directly by the user, as the world handles creation of entities.
	 * </p>
	 *
	 * @param world
	 *			the world to create the entity in
	 * @param id
	 *			the id to set
	 */
	protected Entity(World world, int id) {
		this(world, id, null);
	}

	/**
	 * Creates a new {@link Entity} instance in the given world.
	 * <p>
	 * This will only be called by the world via it's entity manager,
	 * and not directly by the user, as the world handles creation of entities.
	 * </p>
	 *
	 * @param world
	 *			the world to create the entity in
	 * @param id
	 *			the id to set
	 * @param uuid
	 *			the UUID to set
	 */
	protected Entity(World world, int id, UUID uuid) {
		this.world = world;
		this.id = id;
		
		if (uuid != null && world.hasUuidManager())
			world.getManager(UuidEntityManager.class).setUuid(this, uuid);
	}

	
	/**
	 * The internal id for this entity within the framework.
	 * <p>
	 * No other entity will have the same ID, but ID's are however reused so
	 * another entity may acquire this ID if the previous entity was deleted.
	 * </p>
	 * 
	 * @return id of the entity
	 */
	public int getId() {
		return id;
	}

	public static Entity createFlyweight(World world) {
		return new Entity(world, -1);
	}

	/**
	 * Returns a BitSet instance containing bits of the components the entity
	 * possesses.
	 *
	 * @return a BitSet containing the entities component bits
	 */
	protected BitSet getComponentBits() {
		return world.getEntityManager().componentBits(id);
	}
	
	public EntityEdit edit() {
		Entity entity = world.getEntity(id);
		if (entity == null)
			entity = this;

		return world.editPool.obtainEditor(entity);
	}
	

	@Override
	public String toString() {
		return "Entity[" + id + "]";
	}
	
	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public <T extends Component> T createComponent(Class<T> componentKlazz) {
		return edit().create(componentKlazz);
	}

	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public Entity addComponent(Component component) {
		edit().add(component);
		return this;
	}
	
	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public Entity addComponent(Component component, ComponentType type) {
		edit().add(component, type);
		return this;
	}

	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public Entity removeComponent(Component component) {
		edit().remove(component);
		return this;
	}

	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public Entity removeComponent(ComponentType type) {
		edit().remove(type);
		return this;
	}
	
	/**
	 * @deprecated See {@link Entity#edit()}
	 */
	@Deprecated
	public Entity removeComponent(Class<? extends Component> type) {
		edit().remove(type);
		return this;
	}

	/**
	 * Checks if the entity has been added to the world and has not been
	 * deleted from it.
	 * <p>
	 * If the entity has been disabled this will still return true.
	 * </p>
	 *
	 * @return {@code true} if it's active
	 */
	public boolean isActive() {
		return world.getEntityManager().isActive(id);
	}
	
	/**
	 * Will check if the entity is enabled in the world.
	 * <p>
	 * By default all entities that are added to world are enabled, this will
	 * only return false if an entity has been explicitly disabled.
	 * </p>
	 * 
	 * @return {@code true} if it's enabled
	 * @deprecated use components to implement state instead.
	 */
	@Deprecated
	public boolean isEnabled() {
		return world.getEntityManager().isEnabled(id);
	}
	
	/**
	 * Retrieves component from this entity.
	 * <p>
	 * It will provide good performance. But the recommended way to retrieve
	 * components from an entity is using the ComponentMapper.
	 * </p>
	 * 
	 * @param type
	 *			in order to retrieve the component fast you must provide a
	 *			ComponentType instance for the expected component
	 *
	 * @return
	 */
	public Component getComponent(ComponentType type) {
		return world.getComponentManager().getComponent(this, type);
	}

	/**
	 * Slower retrieval of components from this entity.
	 * <p>
	 * Minimize usage of this, but is fine to use e.g. when creating new
	 * entities and setting data in components.
	 * </p>
	 *
	 * @param <T>
	 *			the expected return component class type
	 * @param type
	 *			the expected return component class type
	 *
	 * @return component that matches, or null if none is found
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> type) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		return (T)getComponent(tf.getTypeFor(type));
	}

	/**
	 * Returns a bag of all components this entity has.
	 * <p>
	 * You need to reset the bag yourself if you intend to fill it more than
	 * once.
	 * </p>
	 * 
	 * @param fillBag
	 *			the bag to put the components into
	 *
	 * @return the fillBag containing the components
	 */
	public Bag<Component> getComponents(Bag<Component> fillBag) {
		return world.getComponentManager().getComponentsFor(this, fillBag);
	}

	/**
	 * @deprecated Automatically managed.
	 */
	@Deprecated
	public void addToWorld() {}
	
	/**
	 * @deprecated Automatically managed.
	 */
	@Deprecated
	public void changedInWorld() {}

	/**
	 * Delete this entity from the world.
	 */
	public void deleteFromWorld() {
		edit().deleteEntity();
	}
	
	/**
	 * (Re)enable the entity in the world, after it having being disabled.
	 * <p>
	 * Won't do anything unless it was already disabled.
	 * </p>
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	public void enable() {
		world.enable(this);
	}
	
	/**
	 * Disable the entity from being processed.
	 * <p>
	 * Won't delete it, it will continue to exist but won't get processed.
	 * </p>
	 * @deprecated create your own components to track state.
	 */
	@Deprecated
	public void disable() {
		world.disable(this);
	}
	
	/**
	 * Get the UUID for this entity.
	 * <p>
	 * This UUID is unique per entity (re-used entities get a new UUID).
	 * </p>
	 *
	 * @return uuid instance for this entity
	 * @deprecated historical left-over: use the UuidEntityManager directly, if you need it.
	 */
	@Deprecated
	public UUID getUuid() {
		if (!world.hasUuidManager())
			throw new MundaneWireException(UuidEntityManager.class);
		
		return world.getManager(UuidEntityManager.class).getUuid(this);
	}

	/**
	 * @deprecated historical left-over: use the UuidEntityManager directly, if you need it.
	 */
	@Deprecated
	public void setUuid(UUID uuid) {
		if (world.hasUuidManager()) {
			 world.getManager(UuidEntityManager.class).setUuid(this, uuid);
		}
	}

	/**
	 * Returns the world this entity belongs to.
	 *
	 * @return world of entity
	 */
	public World getWorld() {
		return world;
	}

	public int getCompositionId() {
		return world.getEntityManager().getIdentity(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Entity entity = (Entity) o;

		if (id != entity.id) return false;

		return true;
	}

	public boolean equals(Entity o) {
		return o != null && o.id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
