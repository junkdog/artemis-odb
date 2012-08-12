package com.artemis;

import java.util.Iterator;

import com.artemis.utils.Bag;

public class ComponentManager extends Manager {
	private Bag<Bag<Component>> componentsByType;

	public ComponentManager() {
		componentsByType = new Bag<Bag<Component>>(64);
	}

	private void removeComponentsOfEntity(Entity e) {
		for(int a = 0; componentsByType.size() > a; a++) {
			Bag<Component> components = componentsByType.get(a);
			if(components != null && e.getId() < components.size()) {
				components.set(e.getId(), null);
			}
		}
	}
	
	protected void addComponent(Entity e, ComponentType type, Component component) {
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
