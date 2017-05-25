package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;

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
