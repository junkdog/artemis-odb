package com.artemis;

import com.artemis.utils.Bag;

/**
 * Provide high performance component access and mutation from within a System.
 *
 * This is the recommended way to mutate composition and access components.
 * Component Mappers are as fast as Transmuters.
 *
 * @param <A> Component type to map.
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class ComponentMapper<A extends Component> extends BaseComponentMapper<A> {

	/** Holds all components of given type in the world. */
	final Bag<A> components;

	private final EntityTransmuter createTransmuter;
	private final EntityTransmuter removeTransmuter;
	private final ComponentPool pool;


	public ComponentMapper(Class<A> type, World world) {
		super(world.getComponentManager().typeFactory.getTypeFor(type));
		components = new Bag<A>(type);

		pool = (this.type.isPooled)
			? world.getComponentManager().pooledComponents
			: null;

		createTransmuter = new EntityTransmuterFactory(world).add(type).build();
		removeTransmuter = new EntityTransmuterFactory(world).remove(type).build();
	}

	/**
	 * Fast but unsafe retrieval of a component for this entity.
	 * <p>
	 * No bounding checks, so this could throw an
	 * {@link ArrayIndexOutOfBoundsException}, however in most scenarios you
	 * already know the entity possesses this component.
	 * </p>
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component
	 * @throws ArrayIndexOutOfBoundsException
	 */
	@Override
	public A get(int entityId) throws ArrayIndexOutOfBoundsException {
		return components.get(entityId);
	}

	/**
	 * Fast and safe retrieval of a component for this entity by id.
	 * <p>
	 * If the entity does not have this component then null is returned.
	 * </p>
	 *
	 * @param entityId the id of entity that should possess the component
	 * @return the instance of the component
	 */
	@Override
	@Deprecated
	public A getSafe(int entityId) {
		return get(entityId);
	}

	/**
	 * Checks if the entity has this type of component.
	 *
	 * @param entityId the id of entity to check
	 * @return true if the entity has this component type, false if it doesn't
	 */
	@Override
	public boolean has(int entityId) {
		return get(entityId) != null;
	}


	/**
	 * Remove component from entity.
	 * Does nothing if already removed.
	 *
	 * @param entityId
	 */
	@Override
	public void remove(int entityId) {
		A component = get(entityId);
		if (component != null) {
			// running transmuter first, as it performs some validation
			removeTransmuter.transmuteNoOperation(entityId);
			components.fastSet(entityId, null);

			if (pool != null)
				pool.free((PooledComponent) component, type);
		}
	}

	@Override
	protected void internalRemove(int entityId) { // triggers no composition id update
		if (pool != null) {
			A component = get(entityId);
			if (component != null) {
				pool.free((PooledComponent) component, type);
			}
		}

		components.fastSet(entityId, null);
	}

	/**
	 * Create component for this entity.
	 * Will avoid creation if component exists.
	 *
	 * @param entityId the entity that should possess the component
	 * @return the instance of the component.
	 */
	@Override
	public A create(int entityId) {
		A component = get(entityId);
		if (component == null) {
			// running transmuter first, as it performs som validation
			createTransmuter.transmuteNoOperation(entityId);
			component = createNew();
			components.fastSet(entityId, component);
		}

		return component;
	}

	@Override
	public A internalCreate(int entityId) {
		A component = get(entityId);
		if (component == null) {
			component = createNew();
			components.fastSet(entityId, component);
		}

		return component;
	}

	private A createNew() {
		return (pool != null)
			? (A) pool.obtain(type)
			: (A) ComponentManager.newInstance(type.getType());
	}

}
