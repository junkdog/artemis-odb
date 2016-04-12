package com.artemis;

import java.util.BitSet;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;


/**
 * Handles the association between entities and their components.
 * <p>
 * Only one component manager exists per {@link World} instance,
 * managed by the world.
 * </p>
 *
 * @author Arni Arent
 */
@SkipWire
public class ComponentManager extends BaseSystem {

	/** Collects all Entites marked for deletion from this ComponentManager. */
	final ComponentPool pooledComponents;
	private int highestSeenEntityId;
	private Bag<ComponentMapper> mappers = new Bag<ComponentMapper>();
	
	protected final ComponentTypeFactory typeFactory;

	/**
	 * Creates a new instance of {@link ComponentManager}.
	 */
	protected ComponentManager(int entityContainerSize) {
		this.highestSeenEntityId = entityContainerSize;
		pooledComponents = new ComponentPool();

		typeFactory = new ComponentTypeFactory(this);
	}

	@Override
	protected void processSystem() {}

	/**
	 * Create a component of given type by class.
	 * @param owner entity id
	 * @param componentClass class of component to instance.
	 * @return Newly created packed, pooled or basic component.
	 */
	protected <T extends Component> T create(int owner, Class<T> componentClass) {
		return getMapper(componentClass).create(owner);
	}

	protected <T extends Component> ComponentMapper<T> getMapper(Class<T> mapper) {
		ComponentType type = typeFactory.getTypeFor(mapper);
		return mappers.get(type.getIndex());
	}

	void registerComponentType(ComponentType ct) {
		int index = ct.getIndex();
		mappers.set(index, new ComponentMapper(ct.getType(), world));
	}

	@SuppressWarnings("unchecked")
	static <T extends Component> T newInstance(Class<T> componentClass) {
		try {
			return ClassReflection.newInstance(componentClass);
		} catch (ReflectionException e) {
			throw new InvalidComponentException(componentClass, "Unable to instantiate component.", e);
		}
	}

	/**
	 * Removes all components from the entity associated in this manager.
	 *
	 * @param entityId
	 *			the entity to remove components from
	 */
	private void removeComponents(int entityId) {
		BitSet componentBits = world.getEntityManager().componentBits(entityId);
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			mappers.get(i).internalRemove(entityId);
		}
	}


	/**
	 * Get all components from all entities for a given type.
	 *
	 * @param type
	 *			the type of components to get
	 * @return a bag containing all components of the given type
	 */
	protected Bag<Component> getComponentsByType(ComponentType type) {
		return mappers.get(type.getIndex()).components;
	}

   /**
	 * @return Bag of all generated component types, which identify components without having to use classes.
	 */
	public ImmutableBag<ComponentType> getComponentTypes() {
		return typeFactory.types;
	}

	/**
	 * Get a component of an entity.
	 *
	 * @param entityId
	 *			the entity associated with the component
	 * @param type
	 *			the type of component to get
	 * @return the component of given type
	 */
	protected Component getComponent(int entityId, ComponentType type) {
		ComponentMapper mapper = mappers.get(type.getIndex());
		return mapper.getSafe(entityId);
	}

	/**
	 * Get all component associated with an entity.
	 *
	 * @param entityId
	 *			the entity to get components from
	 * @param fillBag
	 *			a bag to be filled with components
	 * @return the {@code fillBag}, filled with the entities components
	 */
	public Bag<Component> getComponentsFor(int entityId, Bag<Component> fillBag) {
		BitSet componentBits = world.getEntityManager().componentBits(entityId);
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			fillBag.add(mappers.get(i).get(entityId));
		}
		
		return fillBag;
	}

	/**
	 * Removes all components from entities marked for deletion.
	 */
	void clean(IntBag deletedIds) {
		int[] ids = deletedIds.getData();
		for(int i = 0, s = deletedIds.size(); s > i; i++) {
			removeComponents(ids[i]);
		}
	}

	/**
	 * @return Factory responsible for tracking all component types.
	 */
	public ComponentTypeFactory getTypeFactory() {
		return typeFactory;
	}
}
