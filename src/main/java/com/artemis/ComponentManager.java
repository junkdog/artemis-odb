package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;


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
	/** Holds all components grouped by type. */
	private final Bag<PackedComponent> packedComponents;
	/** Collects all Entites marked for deletion from this ComponentManager. */
	private final WildBag<Entity> deleted;


	/**
	 * Creates a new instance of {@link ComponentManager}.
	 */
	public ComponentManager() {
		componentsByType = new Bag<Bag<Component>>();
		packedComponents = new Bag<PackedComponent>();
		deleted = new WildBag<Entity>();
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T create(Class<T> componentClass) {
		ComponentType type = ComponentType.getTypeFor(componentClass);
		if (type.isPackedComponent()) {
			PackedComponent packedComponent = packedComponents.get(type.getIndex());
			if (packedComponent == null) {
				packedComponent = (PackedComponent)newInstance(componentClass);
				packedComponents.set(type.getIndex(), packedComponent);
			}
			return (T)packedComponent;
		}
		
		return newInstance(componentClass);
	}

	private <T extends Component>T newInstance(Class<T> componentClass)
	{
		try	{
			T component = componentClass.newInstance();
			return component;
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void initialize() {
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
			// TODO, reset packed components. but should work (with some dirty data disregarded)
			Bag<Component> componentBag = componentsByType.get(i);
			if (componentBag != null) componentBag.set(e.getId(), null);
		}
		componentBits.clear();
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
			addBasicComponent(e, type, component);

		e.getComponentBits().set(type.getIndex());
	}
	
	private void addPackedComponent(ComponentType type, PackedComponent component)
	{
		packedComponents.ensureCapacity(type.getIndex());
		
		PackedComponent packed = packedComponents.get(type.getIndex());
		if (packed == null) {
			packedComponents.set(type.getIndex(), component);
		}
	}
	
	private void addBasicComponent(Entity e, ComponentType type, Component component)
	{
		componentsByType.ensureCapacity(type.getIndex());
		
		Bag<Component> components = componentsByType.get(type.getIndex());
		if(components == null) {
			components = new Bag<Component>();
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
		if(e.getComponentBits().get(type.getIndex())) {
			if (type.isPackedComponent()) {
				// TODO not really necessary. but might be nice.
//				packedComponents.get(type.getIndex()).reset();
			} else {
				componentsByType.get(type.getIndex()).set(e.getId(), null);
			}
			e.getComponentBits().clear(type.getIndex());
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

		Bag<Component> components = componentsByType.get(type.getIndex());
		if(components == null) {
			components = new Bag<Component>();
			componentsByType.set(type.getIndex(), components);
		}
		return components;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends PackedComponent> T getPackedComponentByType(ComponentType type) {
		if (!type.isPackedComponent())
			throw new RuntimeException(type.getType() + " does not extend " + PackedComponent.class);
		
		PackedComponent component = packedComponents.get(type.getIndex());
		if (component == null) component = (PackedComponent)create(type.getType());
		return (T)component;
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
			PackedComponent component = packedComponents.get(type.getIndex());
			if (component != null) component.setEntityId(e.getId());
			return component;
		} else {
			Bag<Component> components = componentsByType.get(type.getIndex());
			if(components != null) {
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
		// TODO: return persist dinstance of packed components
		BitSet componentBits = e.getComponentBits();

		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			Bag<Component> componentBag = componentsByType.get(i);
			if (componentBag != null) fillBag.add(componentBag.get(e.getId()));
		}
		
		return fillBag;
	}


	@Override
	public void deleted(Entity e) {
		deleted.add(e);
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
