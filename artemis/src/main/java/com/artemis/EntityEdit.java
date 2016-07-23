package com.artemis;


/**
 * Entity mutator.
 * <p/>
 * Provides a fast albeit verbose way to perform batch changes to entities.
 * <p/>
 * {@link com.artemis.BaseSystem}, {@link com.artemis.EntitySubscription.SubscriptionListener}
 * are informed of changes only after the current system has done processing and the next system
 * is about to be invoked. This removes the need for systems to defend their subscription lists
 * and allows for cleaner code and better performance.
 * <p/>
 * Alternatives to edit entities.
 * <p/>
 * - {@link com.artemis.ComponentMapper} is great for concrete changes {@link ComponentMapper#create(Entity)}.
 * Best choice for parameterizing pooled components.
 * - {@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.
 * - {@link com.artemis.EntityTransmuterFactory} Fastest but rigid way of changing entity component compositions.
 * - {@link com.artemis.Archetype} Fastest, low level, no parameterized components.
 */
public final class EntityEdit {

	int entityId;
	private ComponentManager cm;

	EntityEdit(World world) {
		cm = world.getComponentManager();
	}

	/**
	 * Create new instance of component.
	 * <p/>
	 * if exists, replaces and retires old component!
	 *
	 * @param componentKlazz Class to create.
	 * @return Newly instanced component.
	 */
	// FIXME: BREAKING CHANGFE - no longe replaces component (desirable)
	public <T extends Component> T create(Class<T> componentKlazz) {
		return cm.getMapper(componentKlazz).create(entityId);
	}

	/**
	 * Add a component to this entity.
	 *
	 * @param component the component to add to this entity. Does not support packed or pooled.
	 * @return this EntityEdit for chaining
	 * @see #create(Class)
	 */
	public EntityEdit add(Component component) {
		return add(component, cm.typeFactory.getTypeFor(component.getClass()));
	}

	/**
	 * Faster adding of components into the entity.
	 * <p>
	 * Not necessary to use this, but in some cases you might need the extra
	 * performance.
	 * </p>
	 *
	 * @param component the component to add.  Does not support packed or pooled.
	 * @param type      the type of the component
	 * @return this EntityEdit for chaining
	 * @see #create(Class)
	 */
	public EntityEdit add(Component component, ComponentType type) {
		if (type.isPooled) {
			throw new InvalidComponentException(component.getClass(),
				"Use EntityEdit#create(Class<Component>) for adding non-basic component types");
		}

		ComponentMapper mapper = cm.getMapper(type.getType());

		mapper.create(entityId);
		mapper.components.getData()[entityId] =  component;

		return this;
	}

	/**
	 * Get target entity of entity edits.
	 *
	 * @return Entity this EntityEdit operates on.
	 */
	public Entity getEntity() {
		return cm.world.getEntity(entityId);
	}

	/**
	 * Get target entity id of entity edits.
	 *
	 * @return Entity id this EntityEdit operates on.
	 */
	public int getEntityId() {
		return entityId;
	}

	/**
	 * Removes the component from this entity.
	 *
	 * @param component the component to remove from this entity.
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit remove(Component component) {
		return remove(component.getClass());
	}

	/**
	 * Removal of components from a entity.
	 * <p/>
	 * Faster than {@link #remove(Class)}.
	 *
	 * @param type the type of component to remove from this entity
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit remove(ComponentType type) {
		cm.getMapper(type.getType()).remove(entityId);
		return this;
	}

	/**
	 * Remove component by its type.
	 *
	 * @param type the class type of component to remove from this entity
	 * @return this EntityEdit for chaining
	 */
	public EntityEdit remove(Class<? extends Component> type) {
		return remove(cm.typeFactory.getTypeFor(type));
	}

	@Override
	public String toString() {
		return "EntityEdit[" + entityId + "]";
	}
}
