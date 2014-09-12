package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.ReflectionException;


/**
 * Handles the association between entities and their components.
 * <p>
 * Usually only one component manager will exists per {@link World} instance,
 * managed by the world. Entites that add or remove components to them selves
 * will call {@link #addComponent(Entity, ComponentType, Component)} or
 * {@link #removeComponent(Entity, ComponentType)} respectively of the
 * component manager of their world.
 * </p>
 *
 * @author Arni Arent
 */
public class ComponentManager extends Manager {

	/** Holds all components grouped by type. */
	private final Bag<Bag<Component>> componentsByType;
	/** Holds all packed components sorted by type index. */
	private final Bag<PackedComponent> packedComponents;
	private final Bag<BitSet> packedComponentOwners;
	/** Collects all Entites marked for deletion from this ComponentManager. */
	private final WildBag<Entity> deleted;
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
		deleted = new WildBag<Entity>();
		
		typeFactory = new ComponentTypeFactory();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Component> T create(Entity owner, Class<T> componentClass) {
		ComponentType type = typeFactory.getTypeFor(componentClass);
		switch (type.getTaxonomy())
		{
			case BASIC:
				return newInstance(componentClass, false);
			case PACKED:
				PackedComponent packedComponent = packedComponents.safeGet(type.getIndex());
				if (packedComponent == null) {
					packedComponent = (PackedComponent)newInstance(
							componentClass, type.packedHasWorldConstructor);
					packedComponents.set(type.getIndex(), packedComponent);
				}
				getPackedComponentOwners(type).set(owner.getId());
				ensurePackedComponentCapacity(owner);
				packedComponent.forEntity(owner);
				return (T)packedComponent;
			case POOLED:
				try {
					return (T)pooledComponents.obtain((Class<PooledComponent>)componentClass);
				} catch (ReflectionException e) {
					throw new InvalidComponentException(componentClass, "Unable to instantiate component.", e);
				}
			default:
				throw new InvalidComponentException(componentClass, " unknown component type: " + type.getTaxonomy());
		}
	}

	private void ensurePackedComponentCapacity(Entity owner) {
		int id = owner.getId();
		if ((highestSeenEntityId - 1) < id) {
			highestSeenEntityId = id;
			for (int i = 0, s = packedComponents.size(); s > i; i++) {
				PackedComponent component = packedComponents.get(i);
				if (component == null)
					continue;

				component.ensureCapacity(id + 1);
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
	 * @param e
	 *			the entity to remove components from
	 */
	private void removeComponentsOfEntity(Entity e) {
		BitSet componentBits = e.getComponentBits();
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			switch (typeFactory.getTaxonomy(i)) {
				case BASIC:
					componentsByType.get(i).set(e.getId(), null);
					break;
				case POOLED:
					Component pooled = componentsByType.get(i).get(e.getId());
					pooledComponents.free((PooledComponent)pooled);
					componentsByType.get(i).set(e.getId(), null);
					break;
				case PACKED:
					PackedComponent pc = packedComponents.get(i);
					pc.forEntity(e);
					pc.reset();
					break;
				default:
					throw new InvalidComponentException(Component.class, " unknown component type: " + typeFactory.getTaxonomy(i));
			}
		}
//		componentBits.clear();
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
	protected void addComponent(Entity e, ComponentType type, Component component) {
		if (type.isPackedComponent())
			addPackedComponent(type, (PackedComponent)component);
		else
			addBasicComponent(e, type, component); // pooled components are handled the same

//		e.getComponentBits().set(type.getIndex());
	}
	
	private void addPackedComponent(ComponentType type, PackedComponent component) {
		PackedComponent packed = packedComponents.safeGet(type.getIndex());
		if (packed == null) {
			packedComponents.set(type.getIndex(), component);
		}
	}
	
	private void addBasicComponent(Entity e, ComponentType type, Component component)
	{
		Bag<Component> components = componentsByType.safeGet(type.getIndex());
		if(components == null) {
			components = new Bag<Component>(highestSeenEntityId);
			componentsByType.set(type.getIndex(), components);
		}
		
		components.set(e.getId(), component);
	}

	/**
	 * Removes the component of given type from the entity.
	 *
	 * @param e
	 *			the entity to remove from
	 * @param type
	 *			the type of component being removed
	 */
	protected void removeComponent(Entity e, ComponentType type) {
		int index = type.getIndex();
		if(e.getComponentBits().get(index)) {
			switch (type.getTaxonomy()) {
				case BASIC:
					componentsByType.get(index).set(e.getId(), null);
					break;
				case POOLED:
					Component pooled = componentsByType.get(index).get(e.getId());
					pooledComponents.free((PooledComponent)pooled);
					componentsByType.get(index).set(e.getId(), null);
					break;
				case PACKED:
					PackedComponent pc = packedComponents.get(index);
					pc.forEntity(e);
					pc.reset();
					getPackedComponentOwners(type).clear(e.getId());
					break;
				default:
					throw new InvalidComponentException(type.getType(), " unknown component type: " + type.getTaxonomy());
			}
//			e.getComponentBits().clear(index);
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
	 * Get a component of a entity.
	 *
	 * @param e
	 *			the entity associated with the component
	 * @param type
	 *			the type of component to get
	 * @return the component of given type
	 */
	protected Component getComponent(Entity e, ComponentType type) {
		if (type.isPackedComponent()) {
			PackedComponent component = packedComponents.safeGet(type.getIndex());
			if (component != null) component.forEntity(e);
			return component;
		} else {
			Bag<Component> components = componentsByType.safeGet(type.getIndex());
			if (components != null && components.isIndexWithinBounds(e.getId())) {
				return components.get(e.getId());
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
	public Bag<Component> getComponentsFor(Entity e, Bag<Component> fillBag) {
		BitSet componentBits = e.getComponentBits();
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			if (typeFactory.isPackedComponent(i)) {
				fillBag.add(packedComponents.get(i));
			} else {
				fillBag.add(componentsByType.get(i).get(e.getId()));
			}
		}
		
		return fillBag;
	}


	@Override
	public void deleted(Entity e) {
		deleted.add(e);
	}
	
	@Override
	public void added(Entity e) {
		int id = e.getId();
		if ((highestSeenEntityId - 1) < id) {
			ensurePackedComponentCapacity(e);
		}
	}

	/**
	 * Removes all components from entities marked for deletion.
	 */
	protected void clean() {
		int s = deleted.size();
		if(s > 0) {
			Object[] data = deleted.getData();
			for(int i = 0; s > i; i++) {
				removeComponentsOfEntity((Entity)data[i]);
				data[i] = null;
			}
			deleted.setSize(0);
		}
	}
}
