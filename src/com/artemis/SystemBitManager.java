package com.artemis;

import java.util.HashMap;



public class SystemBitManager {
	private static int POS = 0;
	private static HashMap<Class<? extends EntitySystem>, Long> systemBits = new HashMap<Class<? extends EntitySystem>, Long>();
	
	protected static final long getBitFor(Class<? extends EntitySystem> es){
		Long bit = systemBits.get(es);
		
		if(bit == null){
			bit = 1L << POS;
			POS++;
			systemBits.put(es, bit);
		}
		
		return bit;
	}
}
