package com.artemis;

import com.artemis.utils.Bag;


/**
 * High performance component retrieval from entities. Utilized by {@link Component} and {@link PooledComponent}
 * types.
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
class BasicComponentMapper<A extends Component> extends ComponentMapper<A> {

	/** The type of components this mapper handles. */
	private final ComponentType type;
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
	BasicComponentMapper(Class<A> type, World world) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		this.type = tf.getTypeFor(type);
		components = world.getComponentManager().getComponentsByType(this.type);
	}


	@SuppressWarnings("unchecked")
	@Override
	public A get(int entityId) throws ArrayIndexOutOfBoundsException {
		return (A) components.get(entityId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public A getSafe(Entity e) {
		if(components.isIndexWithinBounds(e.getId())) {
			return (A) components.get(e.getId());
		}
		return null;
	}
	
	@Override
	public boolean has(Entity e) {
		return getSafe(e) != null;		
	}


	@Override
	public A get(Entity e, boolean forceNewInstance) throws ArrayIndexOutOfBoundsException {
		return get(e);
	}


	@Override
	public A getSafe(Entity e, boolean forceNewInstance) {
		return getSafe(e);
	}
}
