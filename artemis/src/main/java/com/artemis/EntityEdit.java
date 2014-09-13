package com.artemis;

import java.util.BitSet;

import com.artemis.ComponentType.Taxonomy;

public final class EntityEdit {
	Entity entity;
	private World world;
	boolean hasBeenAddedToWorld;
	final BitSet componentBits;
	boolean scheduledDeletion;
	
	EntityEdit(World world) {
		this.world = world;
		componentBits = new BitSet();
	}

	public void deleteEntity() {
		scheduledDeletion = true;
	}
	
	public <T extends Component> T createComponent(Class<T> componentKlazz) {
		ComponentManager componentManager = world.getComponentManager();
		T component = componentManager.create(entity, componentKlazz);
		
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		ComponentType componentType = tf.getTypeFor(componentKlazz);
		componentManager.addComponent(entity, componentType, component);

		componentBits.set(componentType.getIndex());
		
		return component;
	}
	
	/**
	 * Add a component to this entity.
	 * 
	 * @param component
	 *			the component to add to this entity
	 * 
	 * @return this entity for chaining
	 * @see {@link #createComponent(Class)}
	 */
	public EntityEdit addComponent(Component component) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		addComponent(component, tf.getTypeFor(component.getClass()));
		return this;
	}
	
	/**
	 * Faster adding of components into the entity.
	 * <p>
	 * Not necessary to use this, but in some cases you might need the extra
	 * performance.
	 * </p>
	 *
	 * @param component
	 *			the component to add
	 * @param type
	 *			the type of the component
	 * 
	 * @return this entity for chaining
	 * @see #createComponent(Class)
	 */
	public EntityEdit addComponent(Component component, ComponentType type) {
		if (type.getTaxonomy() != Taxonomy.BASIC) {
			throw new InvalidComponentException(component.getClass(),
				"Use Entity#createComponent for adding non-basic component types");
		}
		world.getComponentManager().addComponent(entity, type, component);
		
		componentBits.set(type.getIndex());
		
		return this;
	}
	
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Removes the component from this entity.
	 * 
	 * @param component
	 *			the component to remove from this entity.
	 */
	public void removeComponent(Component component) {
		removeComponent(component.getClass());
	}

	/**
	 * Faster removal of components from a entity.
	 * 
	 * @param type
	 *			the type of component to remove from this entity
	 */
	public void removeComponent(ComponentType type) {
		world.getComponentManager().removeComponent(entity, type);
		componentBits.clear(type.getIndex());
	}
	
	/**
	 * Remove component by its type.
	 *
	 * @param type
	 *			the class type of component to remove from this entity
	 */
	public void removeComponent(Class<? extends Component> type) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		removeComponent(tf.getTypeFor(type));
	}
	
	@Override
	public String toString() {
		return "EntityEdit[" + entity.getId() + "]";
	}
}