package com.artemis;

import java.util.HashMap;

public class ComponentType {
	private static int INDEX = 0;

	private final int index;
	private final Class<? extends Component> type;

	private ComponentType(Class<? extends Component> type) {
		index = INDEX++;
		this.type = type;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return "ComponentType["+type.getSimpleName()+"] ("+index+")";
	}

	private static HashMap<Class<? extends Component>, ComponentType> componentTypes = new HashMap<Class<? extends Component>, ComponentType>();

	public static ComponentType getTypeFor(Class<? extends Component> c) {
		ComponentType type = componentTypes.get(c);

		if (type == null) {
			type = new ComponentType(c);
			componentTypes.put(c, type);
		}

		return type;
	}

	public static int getIndexFor(Class<? extends Component> c) {
		return getTypeFor(c).getIndex();
	}
}
