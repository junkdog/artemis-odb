package com.artemis;

public abstract class ComponentMapper<A extends Component> {

	private final EntityTransmuter createTransmuter;
	private final EntityTransmuter removeTransmuter;
	private final int flyweight;

	/** The type of components this mapper handles. */
	public final ComponentType type;
	
	public ComponentMapper(Class<A> type, World world) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		this.type = tf.getTypeFor(type);
		createTransmuter = new EntityTransmuterFactory(world).add(type).build();
		removeTransmuter = new EntityTransmuterFactory(world).remove(type).build();
		flyweight = world.getEntityManager()
						.createFlyweight();	
	}
	
	public abstract A get(int entityId) throws ArrayIndexOutOfBoundsException;

	/**
	 * Fast but unsafe retrieval of a component for this entity, by id.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsException}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 *
	 * @param entityId         the entity that should possess the component
	 * @param forceNewInstance Returns a new instance of the component (only applies to {@link PackedComponent}s)
	 * @return the instance of the component
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public abstract A get(int entityId, boolean forceNewInstance) throws ArrayIndexOutOfBoundsException;

	/**
	 * Fast and safe retrieval of a component for this entity by id.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 *
	 * @param entityId the id of entity that should possess the component
	 * @return the instance of the component
	 */
	public abstract A getSafe(int entityId);

	/**
	 * Fast and safe retrieval of a component for this entity, by id.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 *
	 * @param entityId         the entity id that should possess the component
	 * @param forceNewInstance If true, returns a new instance of the component (only applies to {@link PackedComponent}s)
	 * @return the instance of the component
	 */
	public abstract A getSafe(int entityId, boolean forceNewInstance);

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param entityId the id of entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public abstract boolean has(int entityId);


	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entityId
	 */
	public void remove(int entityId) {
		if (has(entityId)) {
			removeTransmuter.transmute(entityId);
		}
	}

	/**
	 * Create component for this entity.
	 * Will avoid creation if component preexists.
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component.
	 */
	public A create(int entityId) {
		A component = getSafe(entityId);
		if (component == null) {
			createTransmuter.transmute(entityId);
			component = get(entityId);
		}
		return component;
	}

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * If the entity does not have this component then fallback is returned.
	 *
	 * @param entityId EntityHelper that should possess the component
	 * @param fallback fallback component to return, or {@code null} to return null.
	 * @return the instance of the component
	 */
	public A getSafe(int entityId, A fallback) {
		final A c = getSafe(entityId);
		return (c != null) ? c : fallback;
	}

	/**
	 * Create or remove a component from an entity.
	 *
	 * Does nothing if already removed or created respectively.
	 *
	 * @param entityId EntityHelper id to change.
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
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		if (tf.getTypeFor(type).isPackedComponent())
			return (ComponentMapper<T>) PackedComponentMapper.create((Class<PackedComponent>) type, world);
		else
			return new BasicComponentMapper<T>(type, world);
	}
}
