package com.artemis;

public abstract class BaseComponentMapper<A extends Component> {
	/** The type of components this mapper handles. */
	public final ComponentType type;

	protected BaseComponentMapper(ComponentType type) {
		this.type = type;
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
	public static <T extends Component> BaseComponentMapper<T> getFor(Class<T> type, World world) {
		return world.getMapper(type);
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

	public abstract A get(int entityId) throws ArrayIndexOutOfBoundsException;

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

	@Deprecated
	public abstract A getSafe(int entityId);

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param e the entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public boolean has(Entity e) throws ArrayIndexOutOfBoundsException {
		return has(e.getId());
	}

	public abstract boolean has(int entityId);

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

	public abstract void remove(int entityId);

	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entity entity to remove.
	 */
	public void remove(Entity entity) {
		remove(entity.getId());
	}

	protected abstract void internalRemove(int entityId);

	public abstract A create(int entityId);

	public abstract A internalCreate(int entityId);

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

	@Override
	public String toString() {
		return "ComponentMapper[" + type.getType().getSimpleName() + ']';
	}
}
