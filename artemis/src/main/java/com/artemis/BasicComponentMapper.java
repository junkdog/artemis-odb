package com.artemis;

import com.artemis.utils.Bag;

import static com.artemis.ComponentType.Taxonomy.POOLED;


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

	/** Holds all components of given type in the world. */
	final Bag<A> components;
	private final EntityTransmuter removeTransmuter;
	private final EntityTransmuter createTransmuter;
	private final ComponentPool pool;

	/**
	 * Creates a new {@code ComponentMapper} instance handling the given type
	 * of component for the given world.
	 *
	 * @param type
	 *			the class type of components to handle
	 * @param world
	 *			the world to handle components for
	 */
	BasicComponentMapper(ComponentType type, World world) {
		super((Class<A>) type.getType(), world);
		components = new Bag<A>();

		pool = (type.getTaxonomy() == POOLED)
			? world.getComponentManager().pooledComponents
			: null;

		createTransmuter = new EntityTransmuterFactory(world).add(type.getType()).build();
		removeTransmuter = new EntityTransmuterFactory(world).remove(type.getType()).build();
	}


	@SuppressWarnings("unchecked")
	@Override
	public A get(int entityId) throws ArrayIndexOutOfBoundsException {
		return (A) components.get(entityId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public A getSafe(int entityId) {
		if(components.isIndexWithinBounds(entityId)) {
			return components.get(entityId);
		}
		return null;
	}

	@Override
	public boolean has(int entityId) {
		return getSafe(entityId) != null;
	}

	@Override
	public A get(int entityId, boolean forceNewInstance) throws ArrayIndexOutOfBoundsException {
		return get(entityId);
	}

	@Override
	public A getSafe(int entityId, boolean forceNewInstance) {
		return getSafe(entityId);
	}

	@Override
	public void remove(int entityId) {
		A component = getSafe(entityId);
		if (component != null) {
			// running transmuter first, as it performs som validation
			removeTransmuter.transmuteNoOperation(entityId);

			if (pool != null)
				pool.free((PooledComponent) component, type);
		}

		components.set(entityId, null);
	}

	@Override
	protected void internalRemove(int entityId) { // triggers no composition id update
		A component = getSafe(entityId);
		if (component != null && pool != null)
			pool.free((PooledComponent) component, type);

		components.set(entityId, null);
	}

	@Override
	public A create(int entityId) {
		A component = getSafe(entityId);
		if (component == null) {
			// running transmuter first, as it performs som validation
			createTransmuter.transmuteNoOperation(entityId);
			component = createNew();
			components.set(entityId, component);
		}

		return component;
	}

	@Override
	public A internalCreate(int entityId) {
		A component = getSafe(entityId);
		if (component == null) {
			component = createNew();
			components.set(entityId, component);
		}

		return component;
	}

	private A createNew() {
		return (pool != null)
			? (A) pool.obtain(type)
			: (A) ComponentManager.newInstance(type.getType());
	}
}
