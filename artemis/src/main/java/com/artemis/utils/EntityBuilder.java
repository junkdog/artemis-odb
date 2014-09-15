package com.artemis.utils;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;

/**
 * Non-reusable entity creation helper for rapid prototyping.
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
	private final EntityEdit edit;

	/** Begin building new entity.*/
	public EntityBuilder(World world) {
		this.world = world;
		edit = world.createEntity().edit();
	}

	/** Add component to entity. */
	public EntityBuilder with(Component component) {
		edit.addComponent(component);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2) {
		edit.addComponent(component1);
		edit.addComponent(component2);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3) {
		edit.addComponent(component1);
		edit.addComponent(component2);
		edit.addComponent(component3);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4) {
		edit.addComponent(component1);
		edit.addComponent(component2);
		edit.addComponent(component3);
		edit.addComponent(component4);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4, Component component5) {
		edit.addComponent(component1);
		edit.addComponent(component2);
		edit.addComponent(component3);
		edit.addComponent(component4);
		edit.addComponent(component5);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			edit.addComponent(components[i]);
		}
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component) {
		edit.createComponent(component);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2) {
		edit.createComponent(component1);
		edit.createComponent(component2);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3) {
		edit.createComponent(component1);
		edit.createComponent(component2);
		edit.createComponent(component3);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4) {
		edit.createComponent(component1);
		edit.createComponent(component2);
		edit.createComponent(component3);
		edit.createComponent(component4);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4, Class<? extends Component> component5) {
		edit.createComponent(component1);
		edit.createComponent(component2);
		edit.createComponent(component3);
		edit.createComponent(component4);
		edit.createComponent(component5);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component>... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			edit.createComponent(components[i]);
		}
		return this;
	}

	/** Register entity with tag. Requires registered TagManager */
	public EntityBuilder tag(String tag) {
		TagManager tagManager = world.getManager(TagManager.class);
		if ( tagManager == null )
			throw new RuntimeException("Register TagManager with your artemis world.");
		
		tagManager.register(tag, edit.getEntity());
		return this;
	}

	/** Register entity with group. Requires registered TagManager */
	public EntityBuilder group(String group) {
		GroupManager groupManager = world.getManager(GroupManager.class);
		if ( groupManager == null )
			throw new RuntimeException("Register GroupManager with your artemis world.");
		
		groupManager.add(edit.getEntity(), group);
		return this;
	}
	
	/** Register entity with multiple groups. Requires registered TagManager */
	public EntityBuilder groups(String... groups) {
		for (int i = 0; groups.length > i; i++)
			group(groups[i]);
		
		return this;
	}

	/** Assemble, add to world */
	public Entity build() {
		return edit.getEntity();
	}
}
