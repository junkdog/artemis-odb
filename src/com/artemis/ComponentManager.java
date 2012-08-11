package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

public class ComponentManager extends Manager {
	private Bag<Bag<Component>> componentsByType;
	private Bag<Component> entityComponents; // Added for debug support.

	public ComponentManager() {
		componentsByType = new Bag<Bag<Component>>(64);
		entityComponents = new Bag<Component>();
	}

	private void removeComponentsOfEntity(Entity e) {
		for(int a = 0; componentsByType.size() > a; a++) {
			Bag<Component> components = componentsByType.get(a);
			if(components != null && e.getId() < components.size()) {
				components.set(e.getId(), null);
			}
		}
	}
	
	protected void addComponent(Entity e, Component component) {
		ComponentType type = ComponentTypeManager.getTypeFor(component.getClass());
		
		if(type.getId() >= componentsByType.getCapacity()) {
			componentsByType.set(type.getId(), null);
		}
		
		Bag<Component> components = componentsByType.get(type.getId());
		if(components == null) {
			components = new Bag<Component>();
			componentsByType.set(type.getId(), components);
		}
		
		components.set(e.getId(), component);

		e.addTypeBit(type.getBit());
	}
	
	protected void removeComponent(Entity e, Component component) {
		ComponentType type = ComponentTypeManager.getTypeFor(component.getClass());
		removeComponent(e, type);
	}
	
	protected void removeComponent(Entity e, ComponentType type) {
		Bag<Component> components = componentsByType.get(type.getId());
		components.set(e.getId(), null);
		e.removeTypeBit(type.getBit());
	}
	
	protected Component getComponent(Entity e, ComponentType type) {
		Bag<Component> bag = componentsByType.get(type.getId());
		if(bag != null && e.getId() < bag.getCapacity())
			return bag.get(e.getId());
		return null;
	}
	
	protected ImmutableBag<Component> getComponents(Entity e) {
		entityComponents.clear();
		for(int a = 0; componentsByType.getCapacity() > a; a++) {
			Bag<Component> components = componentsByType.get(a);
			if(components != null && e.getId() < components.size()) {
				Component component = components.get(e.getId());
				if(component != null) {
					entityComponents.add(component);
				}
			}
		}
		return entityComponents;
	}

	
	
	@Override
	protected void initialize() {
	}
	
	@Override
	protected void added(Entity e) {
	}
	
	@Override
	protected void changed(Entity e) {
	}

	@Override
	protected void deleted(Entity e) {
		removeComponentsOfEntity(e);
	}

}
