package com.artemis;

import java.util.BitSet;

import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

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
	private final Class<A> classType;
	
	/** Holds all components of given type in the world. */
	private final PackedComponent component;
	private final BitSet owners;

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
		owners = cm.getPackedComponentOwners(ComponentType.getTypeFor(type));
		
		this.classType = type;
		component = newInstance(type);
	}
	
	static PackedComponentMapper<PackedComponent> create(Class<PackedComponent> type, World world) {
		return new PackedComponentMapper<PackedComponent>(type, world);
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
			A c = newInstance(classType);
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

	private A newInstance(Class<A> type) {
		try {
			return (A) ClassReflection.newInstance(classType);
		} catch (ReflectionException e) {
			throw new InvalidComponentException(type, "Unable to instantiate component.", e);
		}
	}
}
