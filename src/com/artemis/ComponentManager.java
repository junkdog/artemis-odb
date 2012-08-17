package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

public class ComponentManager extends Manager {
	private Bag<Bag<Component>> componentsByType;

	public ComponentManager() {
		componentsByType = new Bag<Bag<Component>>();
	}
	
	@Override
	protected void initialize() {
	}

	private void removeComponentsOfEntity(Entity e) {
		BitSet componentBits = e.getComponentBits();
		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			componentsByType.get(i).set(e.getId(), null);
			e.getComponentBits().clear(i);
		}
	}
	
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

	protected void removeComponent(Entity e, ComponentType type) {
		if(e.getComponentBits().get(type.getIndex())) {
			componentsByType.get(type.getIndex()).set(e.getId(), null);
			e.getComponentBits().clear(type.getIndex());
		}
	}
	
	protected Component getComponent(Entity e, ComponentType type) {
		if(e.getComponentBits().get(type.getIndex())) {
			return componentsByType.get(type.getIndex()).get(e.getId());
		}
		return null;
	}
	
	public Bag<Component> getComponentsFor(Entity e, Bag<Component> fillBag) {
		BitSet componentBits = e.getComponentBits();

		for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
			fillBag.add(componentsByType.get(i).get(e.getId()));
		}
		
		return fillBag;
	}

	
	@Override
	public void deleted(Entity e) {
		removeComponentsOfEntity(e);
	}

}
