package com.artemis;

import java.util.HashMap;

public class ComponentType {
	private static long nextBit = 1;
	private static int nextId = 0;
	
	private long bit;
	private int id;
	
	private ComponentType() {
		bit = nextBit;
		nextBit = nextBit << 1;
		id = nextId++;
	}
	
	public long getBit() {
		return bit;
	}
	
	public int getId() {
		return id;
	}
	
	
	
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
