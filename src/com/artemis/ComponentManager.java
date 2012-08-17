package com.artemis;

import java.util.BitSet;
import java.util.Iterator;

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
		for(int a = 0; componentsByType.size() > a; a++) {
			if(componentBits.get(a)) {
				componentsByType.get(a).set(e.getId(), null);
			}
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
		Bag<Component> components = componentsByType.get(type.getIndex());
		components.set(e.getId(), null);
		e.getComponentBits().clear(type.getIndex());
	}
	
	protected Component getComponent(Entity e, ComponentType type) {
		Bag<Component> bag = componentsByType.get(type.getIndex());
		if(bag != null && bag.isIndexWithinBounds(e.getId()))
			return bag.get(e.getId());
		return null;
	}
	
	protected Iterator<Component> getComponentsIteratorFor(final Entity e) {
		return new Iterator<Component>() {
			private int index;
			
			@Override
			public boolean hasNext() {
				for(int a = index; componentsByType.getCapacity() > a; a++) {
					Bag<Component> components = componentsByType.get(a);
					if(components != null && e.getId() < components.size()) {
						Component component = components.get(e.getId());
						if(component != null) {
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public Component next() {
				for(int a = index; componentsByType.getCapacity() > a; a++) {
					Bag<Component> components = componentsByType.get(a);
					if(components != null && e.getId() < components.size()) {
						Component component = components.get(e.getId());
						if(component != null) {
							index++;
							return component;
						}
					}
				}
				return null;
			}

			@Override
			public void remove() {
			}
		};
	}
	
	protected Bag<Component> getComponentsFor(Entity e, Bag<Component> fillBag) {
		for(int a = 0; componentsByType.getCapacity() > a; a++) {
			Bag<Component> components = componentsByType.get(a);
			if(components != null && e.getId() < components.size()) {
				Component component = components.get(e.getId());
				if(component != null) {
					fillBag.add(component);
				}
			}
		}
		return fillBag;
	}

	
	@Override
	public void deleted(Entity e) {
		removeComponentsOfEntity(e);
	}

}
