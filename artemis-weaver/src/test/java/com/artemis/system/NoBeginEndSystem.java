package com.artemis.system;

import com.artemis.BaseSystem;
import com.artemis.NullProfiler;
import com.artemis.annotations.Profile;

@Profile(enabled=true, using=NullProfiler.class)
public class NoBeginEndSystem extends BaseSystem {
	
	@Override
	protected void initialize() {
		System.out.println("hello");
	}
	
	
	@Override
	protected void processSystem() {}
}
