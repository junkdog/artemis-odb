package com.artemis;

public class Aspect {
	private static Aspect empty = new Aspect();
	
	private long typeFlags;
	
	public long getTypeFlags() {
		return typeFlags;
	}
	
	private void addComponentType(ComponentType ct) {
		typeFlags |= ct.getBit();
	}
	
	
	@SafeVarargs
	public static Aspect getAspectFor(Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		
		for (Class<? extends Component> type : types) {
			aspect.addComponentType(ComponentType.getTypeFor(type));
		}
		
		return aspect;
	}
	
	public static Aspect getEmpty() {
		return empty;
	}


}
