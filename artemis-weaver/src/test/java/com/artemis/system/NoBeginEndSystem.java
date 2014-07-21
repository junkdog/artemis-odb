package com.artemis.system;

import com.artemis.NullProfiler;
import com.artemis.annotations.Profile;
import com.artemis.systems.VoidEntitySystem;

@Profile(enabled=true, using=NullProfiler.class)
public class NoBeginEndSystem extends VoidEntitySystem {
	
	@Override
	protected void initialize() {
		System.out.println("hello");
	}
	
	
	@Override
	protected void processSystem() {}
}
