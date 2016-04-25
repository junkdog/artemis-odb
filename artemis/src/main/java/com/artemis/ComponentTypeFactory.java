package com.artemis; 

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.ReflectionException;

/**
 * Tracks all component types in a single world.
 * @see ComponentType
 */
public class ComponentTypeFactory {
	/**
	 * Contains all generated component types, newly generated component types
	 * will be stored here.
	 */
	private final IdentityHashMap<Class<? extends Component>, ComponentType> componentTypes
			= new IdentityHashMap<Class<? extends Component>, ComponentType>();

	private final Bag<ComponentTypeListener> listeners
			= new Bag<ComponentTypeListener>();
	
	/** Index of this component type in componentTypes. */
	final Bag<ComponentType> types = new Bag(ComponentType.class);

	int initialMapperCapacity;
	private final ComponentManager cm;

	public ComponentTypeFactory(ComponentManager cm, int entityContainerSize) {
		this.cm = cm;
		initialMapperCapacity = entityContainerSize;
	}


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

		if (type == null)
			type = createComponentType(c);

		return type;
	}

	private ComponentType createComponentType(Class<? extends Component> c) {
		try {
			Constructor ctor = ClassReflection.getConstructor(c);
			if ((ctor.getModifiers() & Modifier.PUBLIC) == 0)
				throw new InvalidComponentException(c, "missing public constructor");
		} catch (ReflectionException e) {
			throw new InvalidComponentException(c, "missing public constructor", e);
		}

		ComponentType type = new ComponentType(c, types.size());
		componentTypes.put(c, type);
		types.add(type);

		cm.registerComponentType(type, initialMapperCapacity);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onCreated(type);
		}

		return type;
	}

	/**
	 * Gets component type by index.
	 * <p>
	 * @param index maps to {@link ComponentType}
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

	public void register(ComponentTypeListener listener) {
		listeners.add(listener);
		listener.initialize(types);
	}

	public interface ComponentTypeListener {
		void initialize(Bag<ComponentType> registered);
		void onCreated(ComponentType type);
	}
}
