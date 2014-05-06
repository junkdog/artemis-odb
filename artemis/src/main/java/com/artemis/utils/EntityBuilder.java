package com.artemis.utils;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;

/**
 * Entity creation helper for rapid prototyping.
 *
 * Example: new Builder(world)
 * .with(Pos.class, Anim.class)
 * .tag("boss")
 * .group("enemies")
 * .build();
 *
 * @author Daan van Yperen
 * @author Junkdog 
 */
public class EntityBuilder {

	private final World world;
	private final Entity entity;

	/** Begin building new entity.*/
	public EntityBuilder(World world) {
		this.world = world;
		entity = world.createEntity();
	}

	/** Add component to entity. */
	public EntityBuilder with(Component component) {
		entity.addComponent(component);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2) {
		entity.addComponent(component1);
		entity.addComponent(component2);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3) {
		entity.addComponent(component1);
		entity.addComponent(component2);
		entity.addComponent(component3);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4) {
		entity.addComponent(component1);
		entity.addComponent(component2);
		entity.addComponent(component3);
		entity.addComponent(component4);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4, Component component5) {
		entity.addComponent(component1);
		entity.addComponent(component2);
		entity.addComponent(component3);
		entity.addComponent(component4);
		entity.addComponent(component5);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			entity.addComponent(components[i]);
		}
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component> component) {
		entity.createComponent(component);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component> component1, Class<Component> component2) {
		entity.createComponent(component1);
		entity.createComponent(component2);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component> component1, Class<Component> component2, Class<Component> component3) {
		entity.createComponent(component1);
		entity.createComponent(component2);
		entity.createComponent(component3);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component> component1, Class<Component> component2, Class<Component> component3, Class<Component> component4) {
		entity.createComponent(component1);
		entity.createComponent(component2);
		entity.createComponent(component3);
		entity.createComponent(component4);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component> component1, Class<Component> component2, Class<Component> component3, Class<Component> component4, Class<Component> component5) {
		entity.createComponent(component1);
		entity.createComponent(component2);
		entity.createComponent(component3);
		entity.createComponent(component4);
		entity.createComponent(component5);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<Component>... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			entity.createComponent(components[i]);
		}
		return this;
	}

	/** Register entity with tag. Requires registered TagManager */
	public EntityBuilder tag(String tag) {
		TagManager tagManager = world.getManager(TagManager.class);
		if ( tagManager == null )
			throw new RuntimeException("Register TagManager with your artemis world.");
		
		tagManager.register(tag, entity);
		return this;
	}

	/** Register entity with group. Requires registered TagManager */
	public EntityBuilder group(String group) {
		GroupManager groupManager = world.getManager(GroupManager.class);
		if ( groupManager == null )
			throw new RuntimeException("Register GroupManager with your artemis world.");
		
		groupManager.add(entity, group);
		return this;
	}

	/** Assemble, add to world */
	public Entity build() {
		world.addEntity(entity);
		return entity;
	}
}
