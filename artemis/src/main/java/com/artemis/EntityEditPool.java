package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

public class EntityEditPool {
	
	private final Bag<EntityEdit> pool = new Bag<EntityEditPool.EntityEdit>();
	private final World world;
	
	private final WildBag<EntityEdit> changed = new WildBag<EntityEdit>();
	
	EntityEditPool(World world) {
		this.world = world;
	}
	
	public EntityEdit obtainEditor(Entity entity) {
		EntityEdit edit;
		if (pool.isEmpty()) {
			edit = new EntityEdit(world);
		} else {
			edit = pool.removeLast();
			edit.changedComponents.clear();
		}
		
		edit.entity = entity;
		changed.add(edit);
		return edit;
	}
	
	void processEntities() {
		int size = changed.size();
		if (size > 0) {
			Object[] data = changed.getData();
			World w = world;
			for (int i = 0; size > i; i++) {
				w.changedEntity(((EntityEdit)data[i]).entity);
			}
			changed.setSize(0);
		}
	}
	
	private void free(EntityEdit edit) {
		pool.add(edit);
	}
	
	public static final class EntityEdit {
		private Entity entity;
		private BitSet changedComponents;
		private World world;
		
		public EntityEdit(World world) {
			this.world = world;
			changedComponents = new BitSet();
		}

		public <T extends Component> T createComponent(Class<T> componentKlazz) {
			ComponentManager componentManager = world.getComponentManager();
			T component = componentManager.create(entity, componentKlazz);
			
			ComponentTypeFactory tf = world.getComponentManager().typeFactory;
			ComponentType componentType = tf.getTypeFor(componentKlazz);
			componentManager.addComponent(entity, componentType, component);
			
			changedComponents.flip(componentType.getIndex());
			
			return component;
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
			changedComponents.flip(type.getIndex());
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
	}
}
