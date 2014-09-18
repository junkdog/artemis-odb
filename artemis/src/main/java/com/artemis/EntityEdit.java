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
	
	public <T extends Component> T create(Class<T> componentKlazz) {
		return createComponent(componentKlazz);
	}
	
	/**
	 * Add a component to this entity.
	 * 
	 * @param component
	 *			the component to add to this entity
	 * 
	 * @return this EntityEdit for chaining
	 * @see {@link #createComponent(Class)}
	 */
	public EntityEdit addComponent(Component component) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		addComponent(component, tf.getTypeFor(component.getClass()));
		return this;
	}
	
	public EntityEdit add(Component component) {
		return addComponent(component);
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
	 * @return this EntityEdit for chaining
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
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit removeComponent(Component component) {
		return removeComponent(component.getClass());
	}
	
	public EntityEdit remove(Component component) {
		return removeComponent(component);
	}

	/**
	 * Faster removal of components from a entity.
	 * 
	 * @param type
	 *			the type of component to remove from this entity
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit removeComponent(ComponentType type) {
		world.getComponentManager().removeComponent(entity, type);
		componentBits.clear(type.getIndex());
		return this;
	}
	
	/**
	 * Remove component by its type.
	 *
	 * @param type
	 *			the class type of component to remove from this entity
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit removeComponent(Class<? extends Component> type) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		return removeComponent(tf.getTypeFor(type));
	}
	
	public EntityEdit remove(Class<? extends Component> type) {
		return removeComponent(type);
	}
	
	@Override
	public String toString() {
		return "EntityEdit[" + entity.getId() + "]";
	}
}