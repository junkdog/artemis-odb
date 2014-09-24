package com.artemis; 

import java.util.IdentityHashMap;

import com.artemis.ComponentType.Taxonomy;
import com.artemis.utils.Bag;

public class ComponentTypeFactory {
	/**
	 * Contains all generated component types, newly generated component types
	 * will be stored here.
	 */
	private final IdentityHashMap<Class<? extends Component>, ComponentType> componentTypes
			= new IdentityHashMap<Class<? extends Component>, ComponentType>();
	
	/** Amount of generated component types. */
	private int componentTypeCount = 0;
	
	/** Index of this component type in componentTypes. */
	private final Bag<ComponentType> types = new Bag<ComponentType>();
	
	
	/**
	 * Gets the component type for the given component class.
	 * <p>
	 * If no component type exists yet, a new one will be created and stored
	 * for later retrieval.
	 * </p>
	 *
	 * @param c
	 *			the component's class to get the type for
	 *
	 * @return the component's {@link ComponentType}
	 */
	public ComponentType getTypeFor(Class<? extends Component> c) {
		ComponentType type = componentTypes.get(c);

		if (type == null) {
			int index = componentTypeCount++;
			type = new ComponentType(c, index);
			componentTypes.put(c, type);
			types.set(index, type);
		}

		return type;
	}
	
	/**
	 * Gets the component type for the given component class.
	 * <p>
	 *
	 * @param c
	 *			the component's class to get the type for
	 *
	 * @return the component's {@link ComponentType}
	 */
	public ComponentType getTypeFor(int index) {
		return types.get(index);
	}
	
	/**
	 * Get the index of the component type of given component class.
	 *
	 * @param c
	 *			the component class to get the type index for
	 *
	 * @return the component type's index
	 */
	public int getIndexFor(Class<? extends Component> c) {
		return getTypeFor(c).getIndex();
	}
	
	protected Taxonomy getTaxonomy(int index) {
		return types.get(index).getTaxonomy();
	}
	
	protected boolean isPackedComponent(int index) {
		return types.get(index).isPackedComponent();
	}
}
