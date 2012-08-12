package com.artemis;

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
	private ComponentManager componentManager;

	private ComponentMapper(Class<A> type, World world) {
		componentManager = world.getComponentManager();
		this.type = ComponentType.getTypeFor(type);
		this.classType = type;
	}

	public A get(Entity e) {
		return classType.cast(componentManager.getComponent(e, type));
	}

	public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
		return new ComponentMapper<T>(type, world);
	}

}
