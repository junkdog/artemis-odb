package com.artemis;

import java.util.HashMap;

public class ComponentTypeManager {
	private static HashMap<Class<? extends Component>, ComponentType> componentTypes = new HashMap<Class<? extends Component>, ComponentType>();
	
	public static final ComponentType getTypeFor(Class<? extends Component> c){
		ComponentType type = componentTypes.get(c);
		
		if(type == null){
			type = new ComponentType();
			componentTypes.put(c, type);
		}
		
		return type;
	}

	public static long getBit(Class<? extends Component> c){
		return getTypeFor(c).getBit();
	}
	
	public static int getId(Class<? extends Component> c){
		return getTypeFor(c).getId();
	}
}
