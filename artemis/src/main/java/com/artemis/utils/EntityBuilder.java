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
		edit.add(component);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2) {
		edit.add(component1);
		edit.add(component2);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		edit.add(component4);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4, Component component5) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		edit.add(component4);
		edit.add(component5);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			edit.add(components[i]);
		}
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component) {
		edit.create(component);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2) {
		edit.create(component1);
		edit.create(component2);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		edit.create(component4);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4, Class<? extends Component> component5) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		edit.create(component4);
		edit.create(component5);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component>... components) {
		for (int i = 0, n = components.length; i < n; i++) {
			edit.create(components[i]);
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
