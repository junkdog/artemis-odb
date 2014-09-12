package com.artemis;

import java.util.BitSet;

import com.artemis.ComponentType.Taxonomy;
import com.artemis.utils.Bag;

public final class EntityEditPool {
	
	private final Bag<EntityEdit> pool = new Bag<EntityEditPool.EntityEdit>();
	private final World world;
	
	private final WildBag<EntityEdit> changed = new WildBag<EntityEdit>();
	private BitSet changedIds = new BitSet();
	
	EntityEditPool(World world) {
		this.world = world;
	}
	
	boolean isEdited(Entity e) {
		return changedIds.get(e.getId());
	}
	
	EntityEdit obtainEditor(Entity entity) {
		EntityEdit edit;
		if (changedIds.get(entity.getId())) {
			edit = findEntityEdit(entity);
		} else if (pool.isEmpty()) {
			edit = new EntityEdit(world);
			changedIds.set(entity.getId());
			changed.add(edit);
		} else {
			edit = pool.removeLast();
			edit.componentBits.clear();
			changedIds.set(entity.getId());
			changed.add(edit);
		}
		
		edit.entity = entity;
		edit.hasBeenAddedToWorld = world.getEntityManager().isActive(entity.getId());
		return edit;
	}
	
	private EntityEdit findEntityEdit(Entity entity) {
		Object[] data = changed.getData();
		for (int i = 0, s = changed.size(); s > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			if (edit.entity == entity)
				return edit;
		}
		
		throw new RuntimeException();
	}

	void processEntities() {
		int size = changed.size();
		if (size > 0) {
			Object[] data = changed.getData();
			World w = world;
			EntityManager em = w.getEntityManager();
			for (int i = 0; size > i; i++) {
				EntityEdit edit = (EntityEdit)data[i];
				em.updateCompositionIdentity(edit);
				if (edit.hasBeenAddedToWorld)
					w.changedEntity(edit.entity);
				else
					w.addEntity(edit.entity);
				
				pool.add(edit);
			}
			changed.setSize(0);
			changedIds.clear();
		}
	}
	
	public static final class EntityEdit {
		Entity entity;
		private World world;
		boolean hasBeenAddedToWorld;
		final BitSet componentBits;
		
		EntityEdit(World world) {
			this.world = world;
			componentBits = new BitSet();
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
}
