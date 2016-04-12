package com.artemis;

import com.artemis.utils.Bag;

/**
 * Provide high performance component access and mutation from within a System.
 *
 * This is the recommended way to mutate composition and access components.
 * Component Mappers are as fast as Transmuters.
 *
 * @param <A> Component type to map.
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class ComponentMapper<A extends Component> {

	/** The type of components this mapper handles. */
	public final ComponentType type;
	/** Holds all components of given type in the world. */
	final Bag<A> components;

	private final EntityTransmuter createTransmuter;
	private final EntityTransmuter removeTransmuter;
	private final ComponentPool pool;


	public ComponentMapper(Class<A> type, World world) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		this.type = tf.getTypeFor(type);

		components = new Bag<A>();

		pool = (this.type.isPooled)
			? world.getComponentManager().pooledComponents
			: null;

		createTransmuter = new EntityTransmuterFactory(world).add(type).build();
		removeTransmuter = new EntityTransmuterFactory(world).remove(type).build();

	}
	
	/**
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsException}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 *
	 * @param e the entity that should possess the component
	 * @return the instance of the component
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public A get(Entity e) throws ArrayIndexOutOfBoundsException {
		return get(e.getId());
	}

	/**
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsException}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public A get(int entityId) throws ArrayIndexOutOfBoundsException {
		return components.get(entityId);
	}

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 *
	 * @param e the entity that should possess the component
	 * @return the instance of the component
	 */
	@Deprecated
	public A getSafe(Entity e) {
		return get(e.getId());
	}

	/**
	 * Fast and safe retrieval of a component for this entity by id.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 *
	 * @param entityId the id of entity that should possess the component
	 * @return the instance of the component
	 */
	@Deprecated
	public A getSafe(int entityId) {
		return get(entityId);
	}

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param e the entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public boolean has(Entity e) throws ArrayIndexOutOfBoundsException {
		return has(e.getId());
	}

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param entityId the id of entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public boolean has(int entityId) {
		return get(entityId) != null;
	}


	/**
	 * Create component for this entity.
	 * Will avoid creation if component preexists.
	 *
	 * @param entity the entity that should possess the component
	 * @return the instance of the component.
	 */
	public A create(Entity entity) {
		return create(entity.getId());
	}

	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entityId
	 */
	public void remove(int entityId) {
		A component = get(entityId);
		if (component != null) {
			// running transmuter first, as it performs some validation
			removeTransmuter.transmuteNoOperation(entityId);
			components.fastSet(entityId, null);

			if (pool != null)
				pool.free((PooledComponent) component, type);
		}
	}

	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entity entity to remove.
	 */
	public void remove(Entity entity) {
		remove(entity.getId());
	}

	protected void internalRemove(int entityId) { // triggers no composition id update
		if (pool != null) {
			A component = get(entityId);
			if (component != null) {
				pool.free((PooledComponent) component, type);
			}
		}

		components.fastSet(entityId, null);
	}

	/**
	 * Create component for this entity.
	 * Will avoid creation if component exists.
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component.
	 */
	public A create(int entityId) {
		A component = get(entityId);
		if (component == null) {
			// running transmuter first, as it performs som validation
			createTransmuter.transmuteNoOperation(entityId);
			component = createNew();
			components.fastSet(entityId, component);
		}

		return component;
	}

	public A internalCreate(int entityId) {
		A component = get(entityId);
		if (component == null) {
			component = createNew();
			components.fastSet(entityId, component);
		}

		return component;
	}

	private A createNew() {
		return (pool != null)
			? (A) pool.obtain(type)
			: (A) ComponentManager.newInstance(type.getType());
	}

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * If the entity does not have this component then fallback is returned.
	 *
	 * @param entityId Entity that should possess the component
	 * @param fallback fallback component to return, or {@code null} to return null.
	 * @return the instance of the component
	 */
	public A getSafe(int entityId, A fallback) {
		final A c = get(entityId);
		return (c != null) ? c : fallback;
	}

	/**
	 * Create or remove a component from an entity.
	 *
	 * Does nothing if already removed or created respectively.
	 *
	 * @param entityId Entity id to change.
	 * @param value {@code true} to create component (if missing), {@code false} to remove (if exists).
	 * @return the instance of the component, or {@code null} if removed.
	 */
	public A set(int entityId, boolean value) {
		if ( value ) {
			return create(entityId);
		} else {
			remove(entityId);
			return null;
		}
	}

	/**
	 * Create or remove a component from an entity.
	 *
	 * Does nothing if already removed or created respectively.
	 *
	 * @param entity Entity to change.
	 * @param value {@code true} to create component (if missing), {@code false} to remove (if exists).
	 * @return the instance of the component, or {@code null} if removed.
	 */
	public A set(Entity entity, boolean value) {
		return set(entity.getId(), value);
	}

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * If the entity does not have this component then fallback is returned.
	 *
	 * @param entity   Entity that should possess the component
	 * @param fallback fallback component to return, or {@code null} to return null.
	 * @return the instance of the component
	 */
	public A getSafe(Entity entity, A fallback) {
		return getSafe(entity.getId(), fallback);
	}

	/**
	 * Returns the ComponentType of this ComponentMapper.
	 * see {@link ComponentMapper#type}
	 */
	public ComponentType getType() {
		return type;
	}
	
	/**
	 * Returns a component mapper for this type of components.
	 *
	 * @param <T>   the class type of components
	 * @param type  the class of components this mapper uses
	 * @param world the world that this component mapper should use
	 * @return a new mapper
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
		return world.getMapper(type);
	}

	@Override
	public String toString() {
		return "ComponentMapper[" + type.getType().getSimpleName() + ']';
	}
}
