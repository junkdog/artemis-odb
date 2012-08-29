package com.artemis;

import java.util.BitSet;

public class Aspect {
	
	private BitSet bitSet;
	private BitSet exclusionSet;
	
	private Aspect() {
		this.bitSet = new BitSet();
		this.exclusionSet = new BitSet();
	}
	
	public BitSet getBitSet() {
		return bitSet;
	}
	
	public BitSet getExclusionSet() {
		return exclusionSet;
	}
	
	public Aspect add(Class<? extends Component> type, Class<? extends Component>... types) {
		bitSet.set(ComponentType.getIndexFor(type));
		
		for (Class<? extends Component> t : types) {
			bitSet.set(ComponentType.getIndexFor(t));
		}

		return this;
	}
	
	public Aspect exclude(Class<? extends Component> type, Class<? extends Component>... types) {
		exclusionSet.set(ComponentType.getIndexFor(type));
		
		for (Class<? extends Component> t : types) {
			exclusionSet.set(ComponentType.getIndexFor(t));
		}
		return this;
	}
	
	
	public static Aspect getAspectFor(Class<? extends Component> type, Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		
		aspect.add(type);
		
		for (Class<? extends Component> t : types) {
			aspect.add(t);
		}
		
		return aspect;
	}
	
	public static Aspect getEmpty() {
		return new Aspect();
	}

}
