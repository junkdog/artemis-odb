package com.artemis;

import java.util.BitSet;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
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
public class ComponentManager extends Manager {

	/** Holds all components grouped by type. */
	private final Bag<Bag<Component>> componentsByType;
	/** Holds all packed components sorted by type index. */
	private final Bag<PackedComponent> packedComponents;
	private final Bag<BitSet> packedComponentOwners;
	/** Collects all Entites marked for deletion from this ComponentManager. */
	private final IntBag deleted;
	private final ComponentPool pooledComponents;
	
	private int highestSeenEntityId;
	
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
		deleted = new IntBag();
		
		typeFactory = new ComponentTypeFactory();
	}

	protected <T extends Component> T create(int owner, Class<T> componentClass) {
		ComponentType type = typeFactory.getTypeFor(componentClass);
		T component = create(owner, type);
		return component;
	}

	@SuppressWarnings("unchecked")
	<T extends Component> T create(int owner, ComponentType type) {
		Class<T> componentClass = (Class<T>)type.getType();
		T component = null;
		
		switch (type.getTaxonomy())
		{
			case BASIC:
				component = newInstance(componentClass, false); 
				break;
			case PACKED:
				PackedComponent packedComponent = packedComponents.safeGet(type.getIndex());
				if (packedComponent == null) {
					packedComponent = (PackedComponent)newInstance(
							componentClass, type.packedHasWorldConstructor);
					packedComponents.set(type.getIndex(), packedComponent);
				}
				getPackedComponentOwners(type).set(owner);
				ensurePackedComponentCapacity(owner);
				packedComponent.forEntity(owner);
				component = (T)packedComponent;
				break;
			case POOLED:
				try {
					reclaimPooled(owner, type);
					component = (T)pooledComponents.obtain((Class<PooledComponent>)componentClass, type);
					break;
				} catch (ReflectionException e) {
					throw new InvalidComponentException(componentClass, "Unable to instantiate component.", e);
				}
			default:
				throw new InvalidComponentException(componentClass, " unknown component type: " + type.getTaxonomy());
		}
		
		addComponent(owner, type, component);
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

	protected BitSet getPackedComponentOwners(ComponentType type)
	{
		BitSet owners = packedComponentOwners.safeGet(type.getIndex());
		if (owners == null) {
			owners = new BitSet();
			packedComponentOwners.set(type.getIndex(), owners);
		}
		return owners;
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
			
			if (component instanceof PackedComponent.DisposedWithWorld) {
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
	 * @param e
	 *			the entity to add to
	 * @param type
	 *			the type of component being added
	 * @param component
	 *			the component to add
	 */
	protected void addComponent(int e, ComponentType type, Component component) {
		if (type.isPackedComponent())
			addPackedComponent(type, (PackedComponent)component);
		else
			addBasicComponent(e, type, component); // pooled components are handled the same
	}
	
	protected void addComponents(int e, Archetype archetype) {
		ComponentType[] types = archetype.types;
		for (int i = 0, s = types.length; s > i; i++) {
			create(e, types[i]);
		}
	}
	
	private void addPackedComponent(ComponentType type, PackedComponent component) {
		PackedComponent packed = packedComponents.safeGet(type.getIndex());
		if (packed == null) {
			packedComponents.set(type.getIndex(), component);
		}
	}
	
	private void addBasicComponent(int e, ComponentType type, Component component)
	{
		Bag<Component> components = componentsByType.safeGet(type.getIndex());
		if (components == null) {
			components = new Bag<Component>(highestSeenEntityId);
			componentsByType.set(type.getIndex(), components);
		}
		
		components.set(e, component);
	}

	/**
	 * Removes the component of given type from the entity.
	 *
	 * @param e
	 *			the entity to remove from
	 * @param type
	 *			the type of component being removed
	 */
	protected void removeComponent(int e, ComponentType type) {
		int index = type.getIndex();
		switch (type.getTaxonomy()) {
			case BASIC:
				componentsByType.get(index).set(e, null);
				break;
			case POOLED:
				Component pooled = componentsByType.get(index).get(e);
				pooledComponents.free((PooledComponent)pooled, type);
				componentsByType.get(index).set(e, null);
				break;
			case PACKED:
				PackedComponent pc = packedComponents.get(index);
				pc.forEntity(e);
				pc.reset();
				getPackedComponentOwners(type).clear(e);
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

	public ImmutableBag<ComponentType> getComponentTypes() {
		return typeFactory.types;
	}

	/**
	 * Get a component of an entity.
	 *
	 * @param e
	 *			the entity associated with the component
	 * @param type
	 *			the type of component to get
	 * @return the component of given type
	 */
	protected Component getComponent(int e, ComponentType type) {
		if (type.isPackedComponent()) {
			PackedComponent component = packedComponents.safeGet(type.getIndex());
			if (component != null) component.forEntity(e);
			return component;
		} else {
			Bag<Component> components = componentsByType.safeGet(type.getIndex());
			if (components != null && components.isIndexWithinBounds(e)) {
				return components.get(e);
			}
		}
		return null;
	}

	/**
	 * Get all component associated with an entity.
	 *
	 * @param e
	 *			the entity to get components from
	 * @param fillBag
	 *			a bag to be filled with components
	 * @return the {@code fillBag}, filled with the entities components
	 */
	public Bag<Component> getComponentsFor(int e, Bag<Component> fillBag) {
		BitSet componentBits = EntityHelper.getComponentBits(world, e);
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			if (typeFactory.isPackedComponent(i)) {
				fillBag.add(packedComponents.get(i));
			} else {
				fillBag.add(componentsByType.get(i).get(e));
			}
		}
		
		return fillBag;
	}


	@Override
	public void deleted(int entityId) {
		deleted.add(entityId);
	}
	
	@Override
	public void added(int entityId) {
		if ((highestSeenEntityId - 1) < entityId) {
			ensurePackedComponentCapacity(entityId);
		}
	}

	/**
	 * Removes all components from entities marked for deletion.
	 */
	protected void clean() {
		int s = deleted.size();
		if(s > 0) {
			int[] ids = deleted.getData();
			for(int i = 0; s > i; i++) {
				removeComponents(ids[i]);
			}
			deleted.setSize(0);
		}
	}

	public ComponentTypeFactory getTypeFactory() {
		return typeFactory;
	}
}
