package com.artemis;

import com.artemis.utils.Bag;


/**
 * High performance component retrieval from entities.
 * <p>
 * Use this wherever you need to retrieve components from entities often and
 * fast.
 * </p>
 *
 * @author Arni Arent
 *
 * @param <A>
 *			the class type of the component
 */
public class ComponentMapper<A extends Component> {

	/** The type of components this mapper handles. */
	private final ComponentType type;
	/** The class of components this mapper handles. */
	private final Class<A> classType;
	/** Holds all components of given type in the world. */
	private final Bag<Component> components;

	
	/**
	 * Creates a new {@code ComponentMapper} instance handling the given type
	 * of component for the given world.
	 *
	 * @param type
	 *			the class type of components to handle
	 * @param world
	 *			the world to handle components for
	 */
	private ComponentMapper(Class<A> type, World world) {
		this.type = ComponentType.getTypeFor(type);
		components = world.getComponentManager().getComponentsByType(this.type);
		this.classType = type;
	}


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
		return classType.cast(components.get(e.getId()));
	}

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
	public A getSafe(Entity e) {
		if(components.isIndexWithinBounds(e.getId())) {
			return classType.cast(components.get(e.getId()));
		}
		return null;
	}
	
	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param e
	 *			the entity to check
	 *
	 * @return true if the entity has this component type, false if it doesn't
	 */
	public boolean has(Entity e) {
		return getSafe(e) != null;		
	}


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
	public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
		return new ComponentMapper<T>(type, world);
	}

}
