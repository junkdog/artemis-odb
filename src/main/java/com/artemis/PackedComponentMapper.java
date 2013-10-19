package com.artemis;



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
class PackedComponentMapper<A extends PackedComponent> extends ComponentMapper<A> {

	/** The class of components this mapper handles. */
	private final Class<A> classType;
	/** Holds all components of given type in the world. */
	
	private PackedComponent component;

	
	/**
	 * Creates a new {@code ComponentMapper} instance handling the given type
	 * of component for the given world.
	 *
	 * @param type
	 *			the class type of components to handle
	 * @param world
	 *			the world to handle components for
	 */
	private PackedComponentMapper(Class<A> type, World world) {
		ComponentManager cm = world.getComponentManager();
		
		this.classType = type;
		try {
			component = classType.newInstance();
		} catch (InstantiationException e) {
			throw new InvalidComponentException(type, "Unable to instantiate component.", e);
		} catch (IllegalAccessException e) {
			throw new InvalidComponentException(type, "Missing public constructor.", e);
		}
	}
	
	static PackedComponentMapper<PackedComponent> create(Class<PackedComponent> type, World world) {
		return new PackedComponentMapper<PackedComponent>(type, world);
	}


	@SuppressWarnings("unchecked")
	@Override
	public A get(Entity e) throws ArrayIndexOutOfBoundsException {
//		A component = classType.cast(components.get(e.getId()));
		component.setEntityId(e.getId());
		
		return (A)component;
	}
	
	@Override
	public A getSafe(Entity e) {
//		if(components.isIndexWithinBounds(e.getId())) {
//			return classType.cast(components.get(e.getId()));
//		}
//		return null;
		throw new RuntimeException("not impl");
	}
	
	@Override
	public boolean has(Entity e) {
//		return getSafe(e) != null;
		// TODO: not done, only checks that the component exists...
		return component != null;
	}
}
