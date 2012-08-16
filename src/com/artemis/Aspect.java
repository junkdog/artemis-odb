package com.artemis;

import java.util.BitSet;

public class Aspect {
	private static BitSet empty = new BitSet();
	
	private BitSet bitSet;
	
	private Aspect(BitSet set) {
		this.bitSet = set;
	}
	
	public BitSet getBitSet() {
		return bitSet;
	}
	
	public static Aspect getAspectFor(Class<? extends Component>... types) {
		BitSet set = new BitSet();
		
		for (Class<? extends Component> type : types) {
			set.set(ComponentType.getIndexFor(type));
		}
		
		return new Aspect(set);
	}
	
	public static BitSet getEmpty() {
		return empty;
	}

}
