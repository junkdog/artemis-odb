package com.artemis;

/**
 * High performance component retrieval from entities. Use this wherever you need
 * to retrieve components from entities often and fast.
 * 
 * @author Arni Arent
 *
 * @param <T>
 */
public class ComponentMapper<T extends Component> {
	private ComponentType type;
	private EntityManager em;
	private Class<T> classType;

	public ComponentMapper(Class<T> type, World world) {
		this.em = world.getEntityManager();
		this.type = ComponentTypeManager.getTypeFor(type);
		this.classType = type;
	}

	public T get(Entity e) {
		return classType.cast(em.getComponent(e, type));
	}

}
