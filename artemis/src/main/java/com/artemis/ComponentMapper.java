package com.artemis;

public abstract class ComponentMapper<A extends Component> {
	/**
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsExeption}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 * 
	 * @param e
	 *			the entity that should possess the component
	 *
	 * @return the instance of the component
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public A get(Entity e) throws ArrayIndexOutOfBoundsException {
		return get(e.getId());
	}

	public abstract A get(int entityId) throws ArrayIndexOutOfBoundsException;

	/**
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsExeption}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 * 
	 * @param e
	 *			the entity that should possess the component
	 * @param forceNewInstance
	 * 			Returns a new instance of the component (only applies to {@link PackedComponent}s)
	 *
	 * @return the instance of the component
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public abstract A get(Entity e, boolean forceNewInstance) throws ArrayIndexOutOfBoundsException;

	/**
	 * Fast and safe retrieval of a component for this entity.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 * 
	 * @param e
	 *			the entity that should possess the component
	 *
	 * @return the instance of the component
	 */
	public abstract A getSafe(Entity e);
	
	/**
	 * Fast and safe retrieval of a component for this entity.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 * 
	 * @param e
	 *			the entity that should possess the component
	 * @param forceNewInstance
	 * 			If true, returns a new instance of the component (only applies to {@link PackedComponent}s)
	 *
	 * @return the instance of the component
	 */
	public abstract A getSafe(Entity e, boolean forceNewInstance);

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param e
	 *			the entity to check
	 *
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public abstract boolean has(Entity e);


	/**
	 * Returns a component mapper for this type of components.
	 * 
	 * @param <T>
	 *			the class type of components
	 * @param type
	 *			the class of components this mapper uses
	 * @param world
	 *			the world that this component mapper should use
	 *
	 * @return a new mapper
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		if (tf.getTypeFor(type).isPackedComponent())
			return (ComponentMapper<T>)PackedComponentMapper.create((Class<PackedComponent>)type, world);
		else
			return new BasicComponentMapper<T>(type, world);
	}
}
