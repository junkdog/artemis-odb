package com.artemis;

public class Aspect {
	
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
			aspect.addComponentType(ComponentTypeManager.getTypeFor(type));
		}
		
		return aspect;
	}


}
