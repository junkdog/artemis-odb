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

	/**
	 * Holds all components grouped by type.
	 */
	private final Bag<Bag<Component>> componentsByType;
	/**
	 * Collects all Entites marked for deletion from this ComponentManager.
	 */
	private final WildBag<Entity> deleted;

	/**
	 * Creates a new instance of {@link ComponentManager}.
	 */
	public ComponentManager() {
		componentsByType = new Bag<Bag<Component>>();
		deleted = new WildBag<Entity>();
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
			componentsByType.get(i).set(e.getId(), null);
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
		componentsByType.ensureCapacity(type.getIndex());
		
		Bag<Component> components = componentsByType.get(type.getIndex());
		if(components == null) {
			components = new Bag<Component>();
			componentsByType.set(type.getIndex(), components);
		}
		
		components.set(e.getId(), component);

		e.getComponentBits().set(type.getIndex());
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
			componentsByType.get(type.getIndex()).set(e.getId(), null);
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
		Bag<Component> components = componentsByType.get(type.getIndex());
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
		Bag<Component> components = componentsByType.get(type.getIndex());
		if(components != null) {
			return components.get(e.getId());
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
			fillBag.add(componentsByType.get(i).get(e.getId()));
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
