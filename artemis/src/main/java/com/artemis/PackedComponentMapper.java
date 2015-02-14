package com.artemis;

import java.util.BitSet;

/**
 * High performance packed component retrieval from entities. Each instance
 * holds its own view of the {@link PackedComponent}.
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
	private final Class<A> componentType;
	
	/** Holds all components of given type in the world. */
	private final PackedComponent component;
	private final BitSet owners;
	
	private boolean newInstanceWithWorld = false;

	private World world;

	/**
	 * Creates a new {@code ComponentMapper} instance handling the given type
	 * of component for the given world.
	 *
	 * @param componentType
	 *			the class type of components to handle
	 * @param world
	 *			the world to handle components for
	 */
	private PackedComponentMapper(Class<A> componentType, World world) {
		this.world = world;
		ComponentManager cm = world.getComponentManager();
		ComponentType type = cm.typeFactory.getTypeFor(componentType);
		newInstanceWithWorld = type.packedHasWorldConstructor;
		owners = cm.getPackedComponentOwners(type);
		
		this.componentType = componentType;
		
		component = newInstance();
	}
	
	static PackedComponentMapper<PackedComponent> create(Class<PackedComponent> type, World world) {
		return new PackedComponentMapper<PackedComponent>(type, world);
	}

	@Override
	@SuppressWarnings("unchecked")
	public A get(int entityId) throws ArrayIndexOutOfBoundsException {
		return get(world.getEntity(entityId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public A get(Entity e) throws ArrayIndexOutOfBoundsException {
		component.forEntity(e);
		return (A) component;
	}

	@Override
	public A getSafe(Entity e) {
		return has(e) ? get(e) : null;
	}
	
	@Override
	public boolean has(Entity e) {
		return owners.get(e.getId());
	}

	@Override
	public A get(Entity e, boolean forceNewInstance) throws ArrayIndexOutOfBoundsException {
		if (forceNewInstance) {
			A c = newInstance();
			c.forEntity(e);
			return c;
		} else {
			return get(e);
		}
	}

	@Override
	public A getSafe(Entity e, boolean forceNewInstance) {
		if (has(e)) {
			return get(e, forceNewInstance);
		} else {
			return null;
		}
	}

	private A newInstance() {
		return world.getComponentManager().newInstance(componentType, newInstanceWithWorld);
	}
}
