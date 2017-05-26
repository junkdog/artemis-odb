package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;

public abstract class BaseComponentMapper<A extends Component> {
	public static final String MSG_TYPES_NOT_SUPPORTED_BY_CORE = "Entity type operations not supported. Make sure your World is of the correct type (EntityWorld / CosplayWorld) and you are using artemis-odb-cosplay.";
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
	 *
	 * This method trades performance for safety.
	 *
	 * User is expected to avoid calling this method on recently (in same system) removed components
	 * or invalid entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
	 * or a partially recycled component if called on in-system removed components.
	 *
	 * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
	 * this method from within a subscription listener.
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public abstract A get(int entityId) throws ArrayIndexOutOfBoundsException;

	public abstract boolean has(int entityId);

	public abstract void remove(int entityId);

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
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * This method trades performance for safety.
	 * <p>
	 * User is expected to avoid calling this method on recently (in same system) removed components
	 * or invalid entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
	 * or a partially recycled component if called on in-system removed components.
	 * <p>
	 * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
	 * this method from within a subscription listener.
	 *
	 * @param e the entity that should possess the component
	 * @return the instance of the component.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public A get(Entity e) throws ArrayIndexOutOfBoundsException {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
	}

	/**
	 * Checks if the entity has this type of component.
	 *
	 *
	 * @param e the entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 * @throws UnsupportedOperationException when called on a world that does not support object references.
	 */
	public boolean has(Entity e) throws ArrayIndexOutOfBoundsException {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
	}

	/**
	 * Create component for this entity.
	 * Will avoid creation if component preexists.
	 *
	 * @param entity the entity that should possess the component
	 * @return the instance of the component.
	 * @throws UnsupportedOperationException when called on a world that does not support object references.
	 */
	public A create(Entity entity) {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
	}

	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entity entity to remove.
	 * @throws UnsupportedOperationException when called on a world that does not support object references.
	 */
	public void remove(Entity entity) {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
	}

	/**
	 * Create or remove a component from an entity.
	 * <p>
	 * Does nothing if already removed or created respectively.
	 *
	 * @param entity Entity to change.
	 * @param value  {@code true} to create component (if missing), {@code false} to remove (if exists).
	 * @return the instance of the component, or {@code null} if removed.
	 * @throws UnsupportedOperationException when called on a world that does not support object references.
	 */
	public A set(Entity entity, boolean value) {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
	}

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * If the entity does not have this component then fallback is returned.
	 *
	 * @param entity   Entity that should possess the component
	 * @param fallback fallback component to return, or {@code null} to return null.
	 * @return the instance of the component
	 * @throws UnsupportedOperationException when called on a world that does not support object references.
	 */
	public A getSafe(Entity entity, A fallback) {
		throw new UnsupportedOperationException(MSG_TYPES_NOT_SUPPORTED_BY_CORE);
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
