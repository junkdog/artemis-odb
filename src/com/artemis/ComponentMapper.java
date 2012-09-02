package com.artemis;

import com.artemis.utils.Bag;

/**
 * High performance component retrieval from entities. Use this wherever you
 * need to retrieve components from entities often and fast.
 * 
 * @author Arni Arent
 * 
 * @param <T>
 */
public class ComponentMapper<A extends Component> {
	private ComponentType type;
	private Class<A> classType;
	private Bag<Component> components;

	private ComponentMapper(Class<A> type, World world) {
		this.type = ComponentType.getTypeFor(type);
		components = world.getComponentManager().getComponentsByType(this.type);
		this.classType = type;
	}

	public A get(Entity e) {
		return classType.cast(components.get(e.getId()));
	}

	public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
		return new ComponentMapper<T>(type, world);
	}

}
