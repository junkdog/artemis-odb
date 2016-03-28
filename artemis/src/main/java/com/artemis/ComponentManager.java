package com.artemis;

import java.util.BitSet;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.Aspect.all;


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

	/** Holds all components grouped by type. */
	private final Bag<Bag<Component>> componentsByType;
	/** Holds all packed components sorted by type index. */
	private final Bag<PackedComponent> packedComponents;
	private final Bag<BitSet> packedComponentOwners;
	/** Collects all Entites marked for deletion from this ComponentManager. */
	private final ComponentPool pooledComponents;

	private int highestSeenEntityId;

	private Bag<ComponentMapper> componentMappers = new Bag<ComponentMapper>();
	
	protected final ComponentTypeFactory typeFactory;

	/**
	 * Creates a new instance of {@link ComponentManager}.
	 */
	protected ComponentManager(int entityContainerSize) {
		this.highestSeenEntityId = entityContainerSize;
		componentsByType = new Bag<Bag<Component>>();
		packedComponents = new Bag<PackedComponent>();
		packedComponentOwners = new Bag<BitSet>();
		pooledComponents = new ComponentPool();

		typeFactory = new ComponentTypeFactory();
	}

	@Override
	protected void processSystem() {}

	@Override
	protected void initialize() {
		world.getAspectSubscriptionManager()
				.get(all())
				.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
					@Override
					public void inserted(IntBag entities) {
						added(entities);
					}

					@Override
					public void removed(IntBag entities) {
						deleted(entities);
					}
				});
	}

	/**
	 * Create a component of given type by class.
	 * @param owner entity id
	 * @param componentClass class of component to instance.
	 * @return Newly created packed, pooled or basic component.
	 */
	protected <T extends Component> T create(int owner, Class<T> componentClass) {
		return getMapper(componentClass).create(owner);
	}

	/**
	 * Create a component of given type. Will replace and retire pre-existing components.
	 * @param owner entity id
	 * @param type component to create
	 * @return Newly created packed, pooled or basic component.
	 */
	@SuppressWarnings("unchecked")
	<T extends Component> T create(int owner, ComponentType type) {
		Class<T> componentClass = (Class<T>)type.getType();
		T component = null;
		
		switch (type.getTaxonomy())
		{
			case BASIC:
				component = newInstance(componentClass, false); 
				break;
			case POOLED:
				try {
					reclaimPooled(owner, type);
					component = (T)pooledComponents.obtain((Class<PooledComponent>)componentClass, type);
					break;
				} catch (ReflectionException e) {
					throw new InvalidComponentException(componentClass, "Unable to instantiate component.", e);
				}
			case PACKED:
				component = createPacked(owner, type, componentClass);
				break;
			default:
				throw new InvalidComponentException(componentClass, " unknown component type: " + type.getTaxonomy());
		}
		
		addComponent(owner, type, component);
		return component;
	}

	private <T extends Component> T createPacked(int owner, ComponentType type, Class<T> componentClass) {
		T component;PackedComponent packedComponent = packedComponents.safeGet(type.getIndex());
		if (packedComponent == null) {
			packedComponent = (PackedComponent)newInstance(
					componentClass, type.packedHasWorldConstructor);
			packedComponents.set(type.getIndex(), packedComponent);
		}
		getPackedComponentOwners(type).set(owner);
		ensurePackedComponentCapacity(owner);
		packedComponent.forEntity(owner);
		component = (T)packedComponent;
		return component;
	}

	private void reclaimPooled(int owner, ComponentType type) {
		Bag<Component> components = componentsByType.safeGet(type.getIndex());
		if (components == null)
			return;

		Component old = components.safeGet(owner);
		if (old != null)
			pooledComponents.free((PooledComponent)old, type);
	}

	private void ensurePackedComponentCapacity(int entityId) {
		if ((highestSeenEntityId - 1) < entityId) {
			highestSeenEntityId = entityId;
			for (int i = 0, s = packedComponents.size(); s > i; i++) {
				PackedComponent component = packedComponents.get(i);
				if (component == null)
					continue;

				component.ensureCapacity(entityId + 1);
			}
		}
	}

	protected BitSet getPackedComponentOwners(ComponentType type) {
		BitSet owners = packedComponentOwners.safeGet(type.getIndex());
		if (owners == null) {
			owners = new BitSet();
			packedComponentOwners.set(type.getIndex(), owners);
		}
		return owners;
	}

	protected <T extends Component> ComponentMapper<T> getMapper(Class<T> mapper) {
		ComponentType type = typeFactory.getTypeFor(mapper);
		int index = type.getIndex();
		ComponentMapper cm = componentMappers.safeGet(index);
		if (cm == null) {
			cm = ComponentMapper.getFor(type, world);
			componentMappers.set(index, cm);
		}

		return componentMappers.get(index);
	}

	@SuppressWarnings("unchecked")
	<T extends Component> T newInstance(Class<T> componentClass, boolean constructorHasWorldParameter) {
		try {
			if (constructorHasWorldParameter) {
				Constructor constructor = ClassReflection.getConstructor(componentClass, World.class);
				return (T) constructor.newInstance(world);
			} else {
				return ClassReflection.newInstance(componentClass);
			}
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
			switch (typeFactory.getTaxonomy(i)) {
				case BASIC:
					componentsByType.get(i).set(entityId, null);
					break;
				case POOLED:
					Component pooled = componentsByType.get(i).get(entityId);
					pooledComponents.free((PooledComponent)pooled, i);
					componentsByType.get(i).set(entityId, null);
					break;
				case PACKED:
					PackedComponent pc = packedComponents.get(i);
					pc.forEntity(entityId);
					pc.reset();
					break;
				default:
					throw new InvalidComponentException(Component.class, " unknown component type: " + typeFactory.getTaxonomy(i));
			}
		}
	}

	@Override
	protected void dispose() {
		for (int i = 0, s = packedComponents.size(); s > i; i++) {
			PackedComponent component = packedComponents.get(i);
			if (component == null)
				continue;
			
			if (ClassReflection.isInstance(PackedComponent.DisposedWithWorld.class, component) ) {
				((PackedComponent.DisposedWithWorld)component).free(world);
			}
		}
	}

	/**
	 * Adds the component of the given type to the entity.
	 * <p>
	 * Only one component of given type can be associated with a entity at the
	 * same time.
	 * </p>
	 *
	 * @param entityId
	 *			the entity to add to
	 * @param type
	 *			the type of component being added
	 * @param component
	 *			the component to add
	 */
	protected void addComponent(int entityId, ComponentType type, Component component) {
		if (type.isPackedComponent())
			addPackedComponent(type, (PackedComponent)component);
		else
			addBasicComponent(entityId, type, component); // pooled components are handled the same
	}

	protected void addComponents(int entityId, Archetype archetype) {
		ComponentType[] types = archetype.types;
		for (int i = 0, s = types.length; s > i; i++) {
			create(entityId, types[i]);
		}
	}
	
	private void addPackedComponent(ComponentType type, PackedComponent component) {
		PackedComponent packed = packedComponents.safeGet(type.getIndex());
		if (packed == null) {
			packedComponents.set(type.getIndex(), component);
		}
	}
	
	private void addBasicComponent(int entityId, ComponentType type, Component component) {
		Bag<Component> components = componentsByType.safeGet(type.getIndex());
		if (components == null) {
			components = new Bag<Component>(highestSeenEntityId);
			componentsByType.set(type.getIndex(), components);
		}
		
		components.set(entityId, component);
	}

	/**
	 * Removes the component of given type from the entity.
	 *
	 * @param entityId
	 *			the entity to remove from
	 * @param type
	 *			the type of component being removed
	 */
	protected void removeComponent(int entityId, ComponentType type) {
		int index = type.getIndex();
		switch (type.getTaxonomy()) {
			case BASIC:
				componentsByType.get(index).set(entityId, null);
				break;
			case POOLED:
				Component pooled = componentsByType.get(index).get(entityId);
				pooledComponents.free((PooledComponent)pooled, type);
				componentsByType.get(index).set(entityId, null);
				break;
			case PACKED:
				PackedComponent pc = packedComponents.get(index);
				pc.forEntity(entityId);
				pc.reset();
				getPackedComponentOwners(type).clear(entityId);
				break;
			default:
				throw new InvalidComponentException(type.getType(), " unknown component type: " + type.getTaxonomy());
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
		if (type.isPackedComponent())
			throw new InvalidComponentException(type.getType(), "PackedComponent types aren't supported.");

		Bag<Component> components = componentsByType.safeGet(type.getIndex());
		if(components == null) {
			components = new Bag<Component>();
			componentsByType.set(type.getIndex(), components);
		}
		return components;
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
		if (type.isPackedComponent()) {
			PackedComponent component = packedComponents.safeGet(type.getIndex());
			if (component != null) component.forEntity(entityId);
			return component;
		} else {
			Bag<Component> components = componentsByType.safeGet(type.getIndex());
			if (components != null && components.isIndexWithinBounds(entityId)) {
				return components.get(entityId);
			}
		}
		return null;
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
			if (typeFactory.isPackedComponent(i)) {
				fillBag.add(packedComponents.get(i));
			} else {
				fillBag.add(componentsByType.get(i).get(entityId));
			}
		}
		
		return fillBag;
	}

	void added(IntBag entities) {
		// entities is sorted, so enough to just grab the last entity
		int entityId = entities.get(entities.size() - 1);
		if ((highestSeenEntityId - 1) < entityId) {
			ensurePackedComponentCapacity(entityId);
		}
	}

	/**
	 * Removes all components from entities marked for deletion.
	 */
	void deleted(IntBag deletedIds) {
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
